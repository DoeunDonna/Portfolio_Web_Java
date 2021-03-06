package com.tj.dessert.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.tj.dessert.dao.LearnMeBoardDao;

public class LbWriteService implements DService {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		
		String path = request.getRealPath("SourcePicUp");
		int maxSize = 1024*1024*10;
		MultipartRequest lbRequest = null;
		String[] lbFileName = {"","",""};
		
		try {
			lbRequest = new MultipartRequest(request, path, maxSize, "utf-8", new DefaultFileRenamePolicy());
			Enumeration<String> params = lbRequest.getFileNames();	//file이랑 짝지어진 parameter를 가지고 온다
			
			int i = 2;
			while(params.hasMoreElements()) {
				String param = params.nextElement();
				lbFileName[i] = lbRequest.getFilesystemName(param);
				i--;
			}
			
			String aId = null;
			String cId = lbRequest.getParameter("cId");
			if(cId.equals("")) {
				aId = lbRequest.getParameter("aId");
			}
			String lbSubject = lbRequest.getParameter("lbSubject");
			String lbContent = lbRequest.getParameter("lbContent");
			String lbFileName01 = lbFileName[0];
				if(lbFileName[0] == null) {
					lbFileName01 = "NOIMG.jpg";
				}
			String lbFileName02 = lbFileName[1];
			String lbFileName03 = lbFileName[2];
			
			String lbIp = request.getRemoteAddr(); //자동적으로  ip를 받아오는 함수
			
			LearnMeBoardDao lbDao = new LearnMeBoardDao();
			
			int result = 0;
			if(!cId.equals("") && aId == null) {
				result = lbDao.lbWriteCustomer(cId, lbSubject, lbContent, lbFileName01, lbFileName02, lbFileName03, lbIp);
			}
			if(cId.equals("")&& !aId.equals("")) {
				result = lbDao.lbWriteAdmin(aId, lbSubject, lbContent, lbFileName01, lbFileName02, lbFileName03, lbIp);
			}
			
			if(result == LearnMeBoardDao.SUCCESS) {
				request.setAttribute("resultMsg", "글쓰기 성공");
			}else {
				request.setAttribute("resultMsg", "글쓰기 실패");
				request.setAttribute("error", "error입니다");
			}
		
		} catch (Exception e) {
			System.out.println("LbWriteERROR :"+ e.getMessage());
			request.setAttribute("errorMsg", "5mega이하의 사진을 첨부해주세요");
		}
		
	//서버에 올라간 mPhoto 파일을 소스폴더에 filecopy
		for(String lbf : lbFileName) {
			InputStream is = null;
			OutputStream os = null;
			try {
				File file = new File(path+"\\"+lbf);
				if(file.exists()) {
					is = new FileInputStream(file);
					os = new FileOutputStream("D:/mega-IT/Source/0_Portfolio/ImDessert/WebContent/SourcePicUp/"+lbf);
					byte[] bs = new byte[(int)file.length()];
					while(true) {
						int nByteCnt = is.read(bs);
						if(nByteCnt==-1) {
							break;
						}
						os.write(bs, 0, nByteCnt);
					}
				}
			}catch (Exception e) {
				System.out.println("파일업로드 에러:"+e.getMessage());
			} finally {
				try {
					if(os != null) {
						os.close();
					}
					if(is != null) {
						is.close();
					}
				}catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
		
		}	

	}

}
