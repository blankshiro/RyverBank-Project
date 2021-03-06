package csd.testclient;

public class TestConstants {
    // this class defines constants, values and API endpoints 
    // these info will be used for automated testing of your API for the purpose of grading
    // do not change any values or definitions here

    // manager and analyst accounts should be added to your api on your own before the test starts
    // we will use these accounts to create users, content, etc. in our test
    // management account, role: ROLE_MANAGER
    public static String m_USERNAME = "manager_1";
    public static String m_PASSWORD = "01_manager_01";

    // analyst account, role: ROLE_ANALYST
    public static String a1_USERNAME = "analyst_1";
    public static String a1_PASSWORD = "01_analyst_01";
    public static String a2_USERNAME = "analyst_2";
    public static String a2_PASSWORD = "02_analyst_02";

    // customer accounts, role: ROLE_USER
    // will be added by the tests
    // do not add them to your API in advance
    public static String u1_USERNAME = "good_user_1";
    public static String u1_PASSWORD = "01_user_01";    
    public static String u2_USERNAME = "good_user_2";
    public static String u2_PASSWORD = "02_user_02";    

    // these variables are for saving the ids generated by your api
    // note that the tests will change the below values
    // you do not need to care about these values
    public static int user_id_1 = 123456;
    public static int user_id_2 = 123457;
    public static int account_id_1 = 12345;
    public static int account_id_2 = 12346;

    // initial account balances - will not change during the tests
    // you do not need to care about these values
    public static final double account_balance_1 = 50000.0;
    public static final double account_balance_2 = 100000.0;

    /**
     *   We will assert the following status codes returned from your API:
     *   HTTP status codes for get/post/put/delete:
     * 
     * - 200: successful get/update/delete
     * - 201: successful creation of new resource
     * - 400: request data not valid
     * - 401: user not authenticated
     * - 403: forbidden, role is not suitable to perform action
     * - 404: resource not found
     * - 409: conflict - resource exists
     *
     */
    // enter the base URL of your api here
    public static String baseURL = "https://yourdomainname.com/api";

    // the below variables specify various enpoints of your api
    // together with description of functionalities provided by each endpoint
    ///////////////////////////////////////////////////////////
    
    /**
        Managing customers of the bank.
        No need to manage manager and analyst accounts (we won't test that)
        ROLE_USER: can view own info at customerURL + "/{id}"
        ROLE_USER: can update own phone number, password and address. Updates to other fields should be ignored.
        ROLE_MANAGER: can create/read/update any customers
        Note: manager can deactivate the customer via "update" by setting "active" to false.
        A deactivated user can't login and access his/her account anymore.
        
        Secure authentication will be done via HTTPS and by specifying the following header in all HTTP requests:
        "Authentication: Basic <encoded username/password>"
        We do not test nor support any other modes of authentication.

        Customer info (in JSON):
        {
            "id": (auto-generated by your api, int value),
            "full_name":"Mark Tan",
            "nric":"S7982834C", (valid nric number)
            "phone":"91251234", (valid SG phone number)
            "address":"27 Jalan Alamak S680234", (string, no need validation)
            "username":"gooduser",
            "password":"password" (need to hash password),
            "authorities":"ROLE_USER",
            "active": true (or false: to indicate if the customer account is in use)
        }

        For our own testing:
        {
            "full_name":"Mark Tan",
            "nric":"S7982834C", 
            "phone":"91251234", 
            "address":"27 Jalan Alamak S680234", 
            "username":"gooduser",
            "password":"password" ,
            "authorities":"ROLE_USER",
            "active": true 
        }
        {
            "full_name":"Doggy Chan",
            "nric":"S79824C",
            "phone":"11251234", 
            "address":"27 Dog S680234", 
            "username":"gooduser1",
            "password":"password1" ,
            "authorities":"ROLE_USER",
            "active": true 
        }
        {
            "full_name":"Kitty Lim",
            "nric":"null",
            "phone":"null",
            "address":"27 Cat S680234",
            "username":"gooduser",
            "password":"password",
            "authorities":"ROLE_USER",
            "active": true 
        }
     */
    public static String customerURL = baseURL + "/customers";
    
    /**
     *  Content management.
        ROLE_USER: view any approved content
        ROLE_ANALYST: CRUD any content
        ROLE_MANAGER: all of the above + approve content
        If the content is not approved yet, it is not visible to customers.

        Content item info:
        {
            "id": (auto-generated by your api, int value),
            "title":"The title of the advisory or news",
            "summary":"The short summary of the content item",
            "content": "The text of the content item",
            "link":"https://link.to.externalcontent",
            "approved": true (or false)
        }
     */
    
    public static String contentURL = baseURL + "/contents";

    /**
     *  Fund transfer.
     *  ROLE_USER: view own accounts via accountURL
        ROLE_USER: view each account info via accountURL + "/{account_id}"
        ROLE_USER: transfer fund via post, view all his/her transactions at accountURL + "/{account_id}" + "/transactions"
        ROLE_MANAGER: open account for customer via accountURL
        SGD fund only.
        Note: our tests do not cover bank account delete/update, nor interest calculation.
        All numbers are double except id (int).

     * Account info:
        {
            "id": (auto-generated by your api),
            "customer_id": 1234,
            "balance": 50000.0,
            "available_balance": 10000.0, (fund can be on-hold due to pending buy trades)
        }
        *** Example: if you place a trade to buy a stock worth $5000 and it's still open,
        *** your available_balance should be balance - $5000. 
        *** You can't transfer fund which is more than available_balance.
 
     * Transfer transaction info:
        {
            "id": (auto-generated by your api),
            "from": (sender_account_id),
            "to": (receiver_account_id),
            "amount": 5000.0
        }
     * 
     */
    public static String accountURL = baseURL + "/accounts";


    /*
     *  ROLE_USER only: get all stock info, or one stock info at stockURL + "/{symbol}"
     * 
     *  All numbers are double except quantity/volume (int) and timestamp (long).
     *  
     *  Market information to support trading.
     *  Market should open from 9am to 5pm on weekdays only.
        
        Stock info data: all components of the Straits Times Index: https://www.sgx.com/indices/products/sti
        Price/volumes could be changing over time to reflect the trade info in the market.
        Note: we will not test add/update/delete stocks.

        Your API should be providing the info below. 
        You can obtain pricing info from external APIs or data sources.
        One example is https://github.com/ranaroussi/yfinance
        Note: it is not required to continuously fetch external pricing info.
        You can just simply obtain the initial static pricing of stocks, 
        and add small random variations to simulate bids/asks, etc.

        The volume/bid/ask info below should reflect the actual trades in your API.
        Note that there can be many different trades with different bid/ask values for a stock,
        your API should only show info for the best trade (best price, or if the prices are 
        the same then trade submitted earlier has the priority).
        
        The last_price should reflect the actual price for the last trade done in your API.
        If no trade has been done, the last_price can be obtained from an external data source.
        Note that bid is always smaller than ask price.

        To be deleted
        Bid price: highest price you can sell. If we are selling stock, look at bid price (highest priced buy order that is available in the market)
        Ask price: lowest price you can buy. If we are buying stock, look at ask price (lowest price that ppl are willing to sell) 
        last price: eg wants to sell house @ 350, receiver offer @325, after nego settle @335 , last price = 335

        A Transaction occurs after the buyer and seller agree on a price for the security which is no higher than a bid and 
        and no lower than a ask

        diff between bid and ask is known as spread; the smaller the spread, the greater the liquidity of the given security

        traders want to get a better price , sell a little higher or buy a little lower

        volume:
        bid volume > ask volume, selling is strong. price is more likely to move down than up
        bid volume < ask volume, buying is strong, price is more likely to move up then down

        end of deletion
        
        Stock info:
        {
            "symbol":"A17U",
            "last_price":3.28,
            "bid_volume":20000,
            "bid":3.26,
            "ask_volume":20000,
            "ask":3.29
        }

        To be deleted
        bid-ask spread benefits the market marker
        the spread represents the market marker's profit

         end of deletion
        

        Market Maker function: your API should be generating various sell and buy orders
            for all traded stocks at appropriate bids and asks (based on actual last_price).
            The volumes for these orders should be specified or fixed by your API, i.e.,
            you can specify any volumes required for testing purposes, e.g., 20000.
            This is to create liquidity in the market, and facilitate fast order matching.
     
        *** Example:
        *** Your API can obtain initial static pricing from https://www.sgx.com/indices/products/sti
        *** When your API starts (or market starts), your API will auto-create multiple open buy and sell trades,
        *** one pair (buy and sell) for each stock listed at the bid and ask price, respectively.
        *** The volumes of these trades can be set to a fixed value, say 20000.

        *** E.g., for A17U, based on the actual last_price of $3.28, you can create a sell trade with ask = $3.29 & volume = 20000,
        *** and a buy trade with bid = $3.26 & volume = 20000.
        
        *** These trades are referred to as the market maker's trades - to create liquidity in the market.
        *** The customers' trades can then be matched with these market maker's trades.

    */
    public static String stockURL = baseURL + "/stocks";

    /** 
     *  For ROLE_USER only: create trade via tradeURL, cancel/view each trade via tradeURL + "/{id}"
     * 
     *  Trade stocks: no short-selling, no contra trading (upfront cash required to buy)
     *  Lot size: 100 (buy or sell have to be in multiples of 100)
     * 
     *  The trade can be a limit order, or a market order
     *   + Limit order: customer can specify any price they like. 
     *     The limit order might not be matched, i.e., it stays open till end of day (5pm) and expires.
     *     ** Example: customer submits a trade to sell at 2x ask price. This order might never be filled.
     * 
     *   + Market order: buy or sell at market price. This kind of order should be matched fast,
     *     subjecting to available volume, but there is no guarantee for the final filled price.
     *     ** Example 1: customer submits a buy trade of 2000 A17U stocks at market price.
     *                   The trade is filled right away by market maker with price of $3.29 (ask price).
     *     ** Example 2: customer submits a sell trade of 40000 A17U stocks at market price.
     *                   The trade is partial-filled right away with quantity of 20000 (market maker's buy), price of $3.26 (bid price).
     *                   If there is no more open trade to fill this trade, it is partial-filled.
     
     *  Trade matching is done according to price/time priority:
     *   + The better-priced trade will be matched first
     *   + If prices are the same, earlier trade will be matched first
     * 
     *   + Buy trades having limit price above market price (current ask) will be matched at current ask.
     *      * Example: a buy trade for A17U with price of $4 will be matched at $3.29 (current ask)
     * 
     *   + Sell trades having limit price below market price (current bid) will be matched at current bid.
     *      * Example: a sell trade for A17U with price of $3 will be match at $3.26 (current bid)
     * 
     *   + One trade can be matched by several other trades depending on the volumes.
     
     *  Settlement will be done via custommer's account (cash settlement)
     * 
     *  Note: a trade might involve one or more fund transfer transactions.
     *  For buy trades, make sure the account balance is enough when doing matching.
     *  If the balance is not enough, the buy trade might be partially filled.
     * 
     *  For sell trades, make sure the customer has the stocks in his portfolio to sell.

     *  All numbers are double except quantity/volume (int) and timestamp (long).
     * 
     * Trade info:
        {
            "id": (auto-generated by your api),
            "action":"buy", (specify "sell" for selling)
            "symbol":"A17U",
            "quantity":1000, (in multiples of 100)
            "bid":3.28, (specify 0.0 for market order, ignored if action is "sell")
            "ask":3.27, (specify 0.0 for market order, ignored if action is "buy")
            "avg_price":3.30 (the average filled price, as one trade can be matched by several other trades)
            "filled_quantity":0;
            "date": (submitted time in Unix timestamp, expired after 5pm of the day),
            "account_id":1234, (account to debit or to deposit fund)
            "customer_id":1234, (submitter of the trade)
            "status":"open" (or "filled", "partial-filled", "cancelled", "expired")
        }
     *  
     */
    public static String tradeURL = baseURL + "/trades";

    /**
        Performance tracking of customer portfolio.
        Show the current stocks owned by customer.
        Show unrealized profit/loss for the stocks owned.
        Show total profit and loss for all trades made.
        
        All numbers are in double, excep quantity (int) or id (int).
        For ROLE_USER only.

        Portfolio info:
        {
                "customer_id": 123456,
                "assests": [
                {
                    "code":"A17U",
                    "quantity":1000,
                    "avg_price": 3.30,
                    "current_price":3.31,
                    "value":3310.0,
                    "gain_loss":10.0
                },
                {
                    "code":"Z74",
                    "avg_price": 2.30,
                    "quantity":2000,
                    "current_price":2.27,
                    "value":4540.0,
                    "gain_loss":-60.0
                }
            ],
            "unrealized_gain_loss":-50.0 (for stocks currently owned),
            "total_gain_loss":500.0 (for all the trades made so far)
        }
     */
    public static String portfolioURL = baseURL + "/portfolio";
}
