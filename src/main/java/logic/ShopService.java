package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dao.BoardDao;
import dao.ItemDao;
import dao.SaleDao;
import dao.SaleItemDao;
import dao.UserDao;

@Service   //@Component + Service 기능. : Controller와 Dao의 중간 연결 역할
public class ShopService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private SaleItemDao saleItemDao;
	@Autowired
	private BoardDao boardDao;
	
	public List<Item> getItemList() {
		return itemDao.list();
	}
	public Item getItem(Integer id) {
		return itemDao.selectOne(id);
	}
	public void itemCreate(Item item, HttpServletRequest request) {
		//item.getPicture() : 업로드된 파일
		if(item.getPicture() != null && !item.getPicture().isEmpty()) {//업로드 파일 존재.
			uploadFileCreate(item.getPicture(),request,"img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		//item 테이블에 내용 저장
		itemDao.insert(item);
	}
	//MultipartFile의 데이터를 파일로 저장
	private void uploadFileCreate
	     (MultipartFile picture, HttpServletRequest request, String path) {
		String orgFile = picture.getOriginalFilename(); //업로드된 파일의 이름
		String uploadPath = request.getServletContext().getRealPath("/") +path;
		File fpath = new File(uploadPath);
		if(!fpath.exists()) fpath.mkdirs(); //업로드 폴더가 없는 경우 폴더 생성
		try {
			//picture : 업로드된 파일의 내용 저장
			// transferTo : 업로드된 파일의 내용을 File로 저장
			picture.transferTo(new File(uploadPath+orgFile));
		} catch(Exception e) {
			e.printStackTrace();
		}				
	}
	public void itemUpdate( Item item, HttpServletRequest request) {
		// TODO Auto-generated method stub
		if(item.getPicture()!=null && !item.getPicture().isEmpty()) {
			uploadFileCreate(item.getPicture(),request,"img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
			
		}
		itemDao.update(item);
	}
	public void itemDelete(Integer id) {
		itemDao.delete(id);
		
	}
	public void insert(User user) {
		userDao.insert(user);
	}
	public User selectUserOne(String userid) {		
		return userDao.selectUserOne(userid);
	}
	public List<Sale> salelist(String id) {
		return saleDao.list(id);
	}
	public List<SaleItem> saleItemList(int saleid) {
		return saleItemDao.list(saleid);
	}
	public void updateUser(User user) {
		userDao.update(user);
	}
	public void userDelete(String userid) {
		userDao.delete(userid);
	}
	public List<User> getUserList() {
		return userDao.list();
	}
	public List<User> getUserList(String[] idchks) {
		return userDao.list(idchks);
	}
	public String getSearch(User user) {
		return userDao.search(user);
	}
	public void userPasswordUpdate(String userid, String pass) {
		userDao.passwordUpdate(userid,pass);		
	}
	/*
	 * sale 정보, saleitem 정보 db에 저장
	 *   1.sale 테이블의 saleid 값을 가져오기. => 최대값 + 1
	 *   2.sale 정보 저장 => insert
	 *   3.Cart 정보에서 saleitem 내용 저장 => insert
	 *   4.sale 객체에 view에서 필요한 정보 저장. 
	 */
	public Sale checkend(User loginUser, Cart cart) {
		// 1.sale 테이블의 saleid 값을 가져오기. => 최대값 + 1
		int maxid = saleDao.getMaxSaleid();
		// 2.sale 정보 저장 => insert
		Sale sale = new Sale();
		sale.setSaleid(maxid+1); //saleid 주문번호 설정
		sale.setUser(loginUser); //주문고객 정보 설정
		sale.setUserid(loginUser.getUserid());
		saleDao.insert(sale);
		//3.Cart 정보에서 saleitem 내용 저장
		int i = 0;
		for(ItemSet iset : cart.getItemSetList()) {
			int seq = ++i;
			SaleItem saleItem = new SaleItem(sale.getSaleid(),seq,iset);
			sale.getItemList().add(saleItem); //sale 객체에 주문상품 객체 추가
			saleItemDao.insert(saleItem);
		}
		//4. sale 객체 리턴
		return sale;
	}
	public int boardcount(String searchtype, String searchcontent) {
		return boardDao.count(searchtype,searchcontent);
	}
	public List<Board> boardlist(Integer pageNum, int limit, 
			             String searchtype, String searchcontent) {
		return boardDao.list(pageNum,limit,searchtype,searchcontent);
	}
	public Board getBoard(Integer num,boolean able) {
	   if(able) boardDao.readcntadd(num);
	   return boardDao.selectOne(num);
	}
	public int maxnum() {
		return boardDao.maxNum();
	}
	public void boardwrite(@Valid Board board, HttpServletRequest request) {
		if(board.getFile1() != null && !board.getFile1().isEmpty()) {
			uploadFileCreate(board.getFile1(), request, "board/file/");
			board.setFileurl(board.getFile1().getOriginalFilename());
		}
		boardDao.write(board);
		
	}
	
	public void boardReply(Board board) {
		//기존의 답글들의 grpstep항목 +1 수정
		boardDao.updateGrpStep(board);
		//답글 등록
		int max = boardDao.maxNum();
		board.setNum(++max);
		board.setGrplevel(board.getGrplevel() + 1);
		board.setGrpstep(board.getGrpstep() + 1);
		boardDao.write(board);
	}
	public void boardUpdate (Board board, HttpServletRequest request) {
		if(board.getFile1()!=null && !board.getFile1().isEmpty()){
			uploadFileCreate(board.getFile1(),request, "board/file/");
			board.setFileurl(board.getFile1().getOriginalFilename());
		}
		boardDao.update(board);
	}
	public void boardDelete(int num) {
		boardDao.delete(num);
	}	
}
    