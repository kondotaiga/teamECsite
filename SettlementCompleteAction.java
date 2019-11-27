package com.internousdev.pumpkin.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.pumpkin.dao.CartInfoDAO;
import com.internousdev.pumpkin.dao.PurchaseHistoryInfoDAO;
import com.internousdev.pumpkin.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class SettlementCompleteAction extends ActionSupport implements SessionAware{

	private String id;
	private Map<String,Object> session;

	public String execute(){

		String tempLogined = String.valueOf(session.get("logined"));
		int logined = "null".equals(tempLogined)? 0 : Integer.parseInt(tempLogined);
		if(logined != 1) {
			return "loginError";
		}

		String result=ERROR;

		//ログインしている場合は、ユーザーIDを取得する。
		String userId=session.get("userId").toString();

		CartInfoDAO cartInfoDAO=new CartInfoDAO();
		List<CartInfoDTO> cartInfoDTOList=cartInfoDAO.getCartInfoDTOList(userId);

		PurchaseHistoryInfoDAO purchaseHistoryInfoDAO=new PurchaseHistoryInfoDAO();
		int count=0;
		//DBの購入履歴テーブルに商品ごとの決済情報を登録する。
		for(CartInfoDTO dto : cartInfoDTOList){
			count += purchaseHistoryInfoDAO.regist(
					userId,
					dto.getProductId(),
					dto.getProductCount(),
					Integer.parseInt(id),
					dto.getPrice()
					);
		}

		//決済完了出来た時に、カートの中身を削除する。
		if(count>0){
			count=cartInfoDAO.deleteAll(String.valueOf(session.get("userId")));
			if(count>0){
				result=SUCCESS;
			}
		}
		return result;
	}

	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id=id;
	}

	public Map<String,Object> getSession(){
		return session;
	}
	public void setSession(Map<String,Object> session){
		this.session=session;
	}
}
