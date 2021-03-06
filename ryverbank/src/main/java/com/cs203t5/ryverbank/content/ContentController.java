package com.cs203t5.ryverbank.content;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.cs203t5.ryverbank.customer.CustomerUnauthorizedException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A ContentController that accepts and returns content JSON data.
 */
@RestController
public class ContentController {
    /** The content repository. */
    private ContentRepository meinContent;
    /** The content services. */
    private ContentService contentService;

    /**
     * Constructs a ContentController with the following parameters.
     * 
     * @param meinContent    The Content Repository
     * @param contentService The Content Services.
     */
    public ContentController(ContentRepository meinContent, ContentService contentService) {
        this.meinContent = meinContent;
        this.contentService = contentService;
    }

    /**
     * Creates a new content. This method should only be accessible by the manager
     * or the analyst. If the user is unauthorized, the method will throw a
     * CustomerUnauthorizedException.
     * 
     * @param aContent The content to be created.
     * @param auth     Checks for user authenticated role.
     * @return The created content.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/contents")
    public Content createContent(@Valid @RequestBody Content aContent, Authentication auth) {
        // Find out the authority of the person logged in
        // Note to self: Find more elegant way to get the authorities
        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();

        // Only allow them to create content if they are either a manager or an analyst
        if (authenticatedUserRole.equals("ROLE_MANAGER") || authenticatedUserRole.equals("ROLE_ANALYST")) {
            if(aContent.isApproved() && authenticatedUserRole.equals("ROLE_ANALYST")){
                aContent.setApproved(false);
            }
            return contentService.createContent(aContent);
        } else {
            // If the user is not authorized to post, then direct them to HTTP403
            throw new CustomerUnauthorizedException("You do not the permission to create content");
        }

    }

    /**
     * List all the contents in the system. This method should only be accessible by
     * the manager. If the method is used by the user, throw a ContentNotFound
     * Exception.
     * 
     * @param auth Checks for authenticated user role.
     * @return The list of all contents.
     */
    @GetMapping("/contents")
    public List<Content> getContents(Authentication auth) {
        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();
        // Testing code
        System.out.println("LOGGED IN AS: " + authenticatedUserRole);
        // Return all content that are approved/non-approved
        if (authenticatedUserRole.equals("ROLE_MANAGER") || authenticatedUserRole.equals("ROLE_ANALYST")) {
            if (meinContent.findAllByOrderByApprovedAsc().isEmpty()) {
                throw new ContentNotFoundException("No content available for viewing");
            }
            return meinContent.findAllByOrderByApprovedAsc();

            // Return all content that are approved
        } else if (authenticatedUserRole.equals("ROLE_USER")) {
            if (meinContent.findByApproved(true).isEmpty()) {
                throw new ContentNotFoundException("No content available for viewing");
            }
            return meinContent.findByApproved(true);
        } else {
            // Code will never reach here if the security config is functional
            throw new CustomerUnauthorizedException("You do not have permission to access the content");
        }
    }

    /**
     * List all the contents in the system. This method should only be accessible by
     * the manager. If the method is used by the user, throw a ContentNotFound
     * Exception.
     * 
     * @param id   The content id.
     * @param auth Checks for authenticated user role.
     * @return The list of all contents.
     */
    @GetMapping("/contents/{id}")
    public Optional<Content> getAContent(@PathVariable Long id, Authentication auth) {
        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();
        // Testing code
        System.out.println("LOGGED IN AS: " + authenticatedUserRole);
        // Return all content that are approved/non-approved
        if (authenticatedUserRole.equals("ROLE_MANAGER") || authenticatedUserRole.equals("ROLE_ANALYST")) {
            if (meinContent.findById(id).isEmpty()) {
                throw new ContentNotFoundException("No content available for viewing");
            }
            // If the person is a user, we need to check if the content is approved or not
        } else if (authenticatedUserRole.equals("ROLE_USER")) {
            if (meinContent.findById(id).isEmpty()) {
                throw new ContentNotFoundException("No content available for viewing");
            }
            Optional<Content> newContent = meinContent.findById(id);
            try {
                Content aContent = newContent.get();
                if (!aContent.isApproved()) {
                    throw new ContentNotFoundException("No content available for viewing");
                }
            } catch (Exception e) {
                throw new ContentNotFoundException("No content available for viewing");
            }
        }

        return meinContent.findById(id);

    }

    /**
     * General method for calling all the updating methods and approving method in
     * the content class. Users with the role of analyst or managers can call the
     * updating methods. Otherwise, only the manager can call the approving method.
     * 
     * @param id       The content id.
     * @param aContent The content to update.
     * @param auth     Checks for authenticated user role.
     * @return The updated content.
     */
    @PutMapping(value = "/contents/{id}")
    public Optional<Content> updateContentFields(@PathVariable Long id, @RequestBody Content aContent,
            Authentication auth) {

        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();
        /*
         * A normal user should not be able to access this page based on the
         * securityConfig
         */
        // If the content does not exist, throw error 404 handled by
        // ContentNotFoundException
        // Code ends here if the book is not found
        if (!meinContent.existsById(id)) {
            throw new ContentNotFoundException(id);
        }
        /*
         * If the Json input passed in is not null for the fields, it means that someone
         * wishes to edit the link This same process is repeated for every field that is
         * available for updates.
         */
        // If the input passed into the Json is not null for the "link" field, it means
        // that someone wishes to edit the link
        if (aContent.getTitle() != null) {
            contentService.updateTitle(id, aContent.getTitle());
        }

        // If the input passed into the Json is not null for the "summary" field, it
        // means that someone wishes to edit the summary
        if (aContent.getSummary() != null) {
            contentService.updateSummary(id, aContent.getSummary());
        }

        // If the input passed into the Json is not null for the "content" field, it
        // means that someone wishes to edit the content
        if (aContent.getNewsContent() != null) {
            contentService.updateContent(id, aContent.getNewsContent());
        }

        // If the input passed into the Json is not null for the "link" field, it means
        // that someone wishes to edit the link
        if (aContent.getLink() != null) {
            contentService.updateLink(id, aContent.getLink());
        }

        // Only managers can access this function, so a role check needs to be performed
        if (aContent.isApproved()) {
            // A nested-if is required here to throw an error if an analyst tries to approve
            // content
            if (authenticatedUserRole.equals("ROLE_MANAGER")) {
                contentService.approveContent(id);
            } else {
                throw new CustomerUnauthorizedException("Analysts cannot approve content");
            }
        }
        return meinContent.findById(id);
    }

    /**
     * Deletes the content with the specified id from the system. If there is no
     * such content, throw a ContentNotFoundException.
     * 
     * @param id The id of the content.
     */
    @DeleteMapping(value = "/contents/{id}")
    public void deleteContent(@PathVariable Long id) {
        try {
            meinContent.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ContentNotFoundException(id);
        }

    }

}
