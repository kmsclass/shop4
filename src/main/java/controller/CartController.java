package controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import exception.CartEmptyException;
import logic.Cart;
import logic.Item;
import logic.ItemSet;
import logic.Sale;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("cart")
public class CartController {
	@Autowired
	private ShopService service;
	
	@RequestMapping("cartAdd")
	public ModelAndView add(Integer id, Integer quantity,HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		//item : id해당하는 item 정보 저장
		Item item = service.getItem(id);
		Cart cart = (Cart)session.getAttribute("CART");
		if(cart == null) {
			cart = new Cart();
			session.setAttribute("CART", cart);
		}
		cart.push(new ItemSet(item,quantity));
		mav.addObject("message",item.getName()+":"+quantity + "개 장바구니 추가");
		mav.addObject("cart",cart);		
		return mav;
	}	
	@RequestMapping("cartDelete")
	public ModelAndView delete(int index,HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Cart cart = (Cart)session.getAttribute("CART");
//		int num=index;
		//robj : 삭제된 객체
		ItemSet robj = cart.getItemSetList().remove(index);
		mav.addObject("message",robj.getItem().getName()+"가(이) 삭제되었습니다.");
		mav.addObject("cart",cart);
		return mav;
	}
	
	@RequestMapping("cartView")
	public ModelAndView view(HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Cart cart = (Cart)session.getAttribute("CART");
		
		if(cart == null || cart.getItemSetList().size() == 0) {
			//throw : 예외 발생
			//throws : 예외 내보내기. 예외처리
			throw new CartEmptyException
			("장바구니에 상품이 없습니다.","../item/list.shop");
		}
		
		mav.addObject("cart",cart);
		return mav;
	}
	/*
	 * 
	 */
	@RequestMapping("checkout") 
    public String checkout(HttpSession session) {
		return null;
	}
	/*
	 * 주문 확정 : 로그인 상태,장바구니에 상품 존재해야함 => aop 이용
	 *   1. 장바구니 상품을 saleitem 테이블에 저장.
	 *   2. 로그인 정보로 주문정보(sale) 테이블에 저장.
	 *   3. 장바구니 상품 제거
	 */
	@RequestMapping("end")
	public ModelAndView checkend (HttpSession session) {
		ModelAndView mav = new ModelAndView();
		Cart cart = (Cart)session.getAttribute("CART");
		User loginUser = (User)session.getAttribute("loginUser");
		Sale sale = service.checkend(loginUser,cart);
		long total = cart.getTotal(); //장바구니상품의 총금액합계 
		session.removeAttribute("CART"); //3. 장바구니 상품 제거
		mav.addObject("sale",sale);
		mav.addObject("total",total);
		return mav;
	}	
}