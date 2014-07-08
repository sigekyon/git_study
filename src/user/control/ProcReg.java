package user.control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import user.bean.RegistrantInfo;
import user.parts.RegInfCheck;
import user.parts.RegInfDAO;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * 雇用者登録を行います。
 * 
 * <br>最終更新日：2014/6/19
 * @version 1.0
 */
public class ProcReg extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String errMsg;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProcReg() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RegInfDAO rDAO = null;
		try {
			request.setCharacterEncoding("UTF-8"); 
			response.setContentType("text/html; charset=UTF-8"); 
			RequestDispatcher rd = null;
			
			InitialContext initCon = new InitialContext();
			DataSource ds = (DataSource)initCon.lookup("java:comp/env/jdbc/Task"); 
			Connection con = ds.getConnection();
			Statement stmt = con.createStatement(); 
			ResultSet result = stmt.executeQuery("select * from registrants;");
			while(result.next()){
				System.out.println(result.getString("registrant_id"));
				System.out.println(result.getString("registrant_name"));
				System.out.println(result.getString("registrant_age"));			
			}
			result.close();
			stmt.close();
			con.close();
			initCon.close();
			
			errMsg = "";
			rDAO = new RegInfDAO();

			// 登録されたパラメータを取得
			String regName = request.getParameter("regName");
			String regAge = request.getParameter("regAge");
			// 新規IDを設定
			String regId = rDAO.getNextId();
		
			inputCheck(regId,regName, regAge);// 入力データに誤りがあった場合はその旨を表示させる

			if(!errMsg.equals("")){
				request.setAttribute("errMsg", errMsg);
				rd = request.getRequestDispatcher("/RegRegist.jsp");
				rd.forward(request, response);
				return ;
			}
			// 入力情報を設定
			RegistrantInfo regInfo = new RegistrantInfo();
			regInfo.setrId(regId);
			regInfo.setrName(regName);
			regInfo.setrAge(regAge);
			rDAO.insert(regId, regName, regAge);

			// データを書き込む
//			WriteRegistInfo.regRegInfo(regInfo);

			// 結果画面へ遷移するための情報を設定
			request.setAttribute("regInfo", regInfo);
			rd = request.getRequestDispatcher("/RegistResult.jsp");

			rd.forward(request, response);
		
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errMsg", "エラーが発生しました。申し訳ありませんがもう一度最初からお願いします。");
			HttpSession session = request.getSession(true);
			session.invalidate();
			RequestDispatcher rd = request.getRequestDispatcher(
					"/Login.jsp");
			rd.forward(request, response);
		} finally {
			if(rDAO != null){
				rDAO.close();
			}
		}
	}

	private void inputCheck(String regId, String regName, String regAge) {
		RegInfCheck richk = new RegInfCheck();
		richk.checkId(regId);
		richk.checkName(regName);
		richk.checkAge(regAge);
		errMsg = richk.getErrMsg();
	}
}
