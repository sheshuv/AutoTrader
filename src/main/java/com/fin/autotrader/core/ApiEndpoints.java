package com.fin.autotrader.core;

public class ApiEndpoints {

	public static final String endpoint = "https://api.shoonya.com/NorenWClientTP";
//	public static final String endpoint = "https://api.shoonya.com/NorenWClientWeb";
	public static final String authorize= "/QuickAuth";
	public static final String logout= "/Logout";
	public static final String forgot_password= "/ForgotPassword";
	public static final String watchlist_names= "/MWList";
	public static final String watchlist= "/MarketWatch";
	public static final String watchlist_add= "/AddMultiScripsToMW";
	public static final String watchlist_delete= "/DeleteMultiMWScrips";
	public static final String placeorder= "/PlaceOrder";
	public static final String modifyorder= "/ModifyOrder";
	public static final String cancelorder= "/CancelOrder";
	public static final String exitorder= "/ExitSNOOrder";
	public static final String orderbook= "/OrderBook";
	public static final String tradebook= "/TradeBook";          
	public static final String singleorderhistory= "/SingleOrdHist";
	public static final String searchscrip= "/SearchScrip";
	public static final String TPSeries= "/TPSeries";     
	public static final String optionchain= "/GetOptionChain";     
	public static final String holdings= "/Holdings";
	public static final String limits= "/Limits";
	public static final String positions= "/PositionBook";
	public static final String scripinfo= "/GetSecurityInfo";
	public static final String getquotes= "/GetQuotes";
	public static final String WEB_SOCKET_ENDPOINT= "wss://shoonyatrade.finvasia.com/NorenWSTP/";
	//NorenWSTP
//	public static final String WEB_SOCKET_ENDPOINT= "wss://shoonyatrade.finvasia.com/NorenWSWeb/";
	
}
