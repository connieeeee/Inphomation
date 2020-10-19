package bit.com.inpho.service.impl;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import bit.com.inpho.dao.MemberDao;
import bit.com.inpho.dto.MemberDto;
import bit.com.inpho.service.MemberService;
import bit.com.inpho.util.MemberUtil;

@Service
public class MemberServiceImpl implements MemberService{
	@Autowired
	MemberDao memberDao;
	
	@Override
	public boolean confirmId(MemberDto member) {
		return memberDao.confirmID(member);
	}
	
	@Override
	public String doLogin(MemberDto member,HttpSession session) {
		MemberDto result = memberDao.doLogin(member);
		System.out.println(result.toString());
		session.setMaxInactiveInterval(24*3600);
		if(result!=null) {
			//로그인 성공시에 loginFail 세션은 전부다 초기화가 된다
			session.removeAttribute("loginFail");
			if(result.getAuth_enabled()==1 && result.getAuth_active()==1) {
				//담긴정보 = seq,email,auth,nickname,create,profile_image
				session.setAttribute("login", result);
				return "success";
			}else if(result.getAuth_enabled()==0 || result.getAuth_active()==0) {
				return "authErr";
			}
		}
		
		//로그인 실패시에 세션저장
		if(session.getAttribute("loginFail")==null) {
			session.setAttribute("loginFail", 1);
		}else {
			int failNum = ((int)session.getAttribute("loginFail"));
			session.setAttribute("loginFail", ++failNum);
		}

		System.out.println("로그인 실패");
		return "fail";
	}

	@Override
	public boolean regeisterMember(MemberDto member) {
		//회원가입 성공
		String authKey = "";
		for(;;) {
			authKey = MemberUtil.makePassword(50);
			if(memberDao.selectAuthKey(authKey)==null) 
				break;
		}
		member.setAuthKey(authKey);
		if(memberDao.regeisterMember(member)>0) {
			if(memberDao.registerAuthKey(member)>0) {
				return true;
			}
		}
		//회원가입 실패
		return false;
	}

	@Override
	public boolean socialLogin(MemberDto member, HttpSession session) {
		MemberDto result =  memberDao.doSocialLogin(member);
		
		if(result==null) {
			//회원이 아닌경우
			int insertResult = memberDao.regeisterSocialMember(member);
			if(insertResult>0) {
				result = memberDao.doSocialLogin(member);
			}else {
				return false;
			}
		}
		session.setAttribute("login", result);
		session.setMaxInactiveInterval(24*3600);
		return true;
	}

	//유저가 이메일로 온링크를 타고 인증을 하려고 하는 메소드 
	@Override
	public boolean idActive(MemberDto member, HttpSession session) {
		MemberDto reqAuthMember = memberDao.selectAuthKey(member.getAuthKey());
		if(reqAuthMember!=null) {
			memberDao.deleteAuthKey(member.getAuthKey());
			reqAuthMember = memberDao.authKeyLogin(reqAuthMember);
			System.out.println(reqAuthMember.toString());
			session.setMaxInactiveInterval(24*3600);
			session.setAttribute("login", reqAuthMember);
			return true;
		}
		
		return false;
	}

	


	
}