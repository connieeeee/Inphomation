package bit.com.inpho.controller;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.scribejava.core.model.OAuth2AccessToken;

import bit.com.inpho.dto.MemberDto;
import bit.com.inpho.service.MemberService;
import bit.com.inpho.util.MemberUtil;

@Controller
public class memberController {
	
	private NaverController naver;
	private String apiResult = null;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private void setNaverLoginController(NaverController naver) {
		this.naver = naver; //naver 서비스생성
	}
	@ResponseBody
	@PostMapping("/sendResetPwd")
	public void sendResetMail(MemberDto member) throws Exception{
		memberService.resetPwdAuthCreate(member);
	}
	@ResponseBody
	@PostMapping("/changePwd")
	public boolean changePwd(MemberDto member) {
		return memberService.changePwd(member);
	}
	
	
	@GetMapping("/resetPassword")
	public String resetPassword(MemberDto member, Model model) {
		//인증키가 없이 들어오는 경우
		if(member.getAuthKey() == null ) 
			model.addAttribute("page","0");
		else{//인증키가 있는 경우
			MemberDto result = memberService.confirmAuthKey(member);
			if(result != null) { //데이터가 있는 경우
				model.addAttribute("page","1");
				model.addAttribute("info",result);
			}else {
				model.addAttribute("page","2");
			}
		}
		return "resetPassword.tiles";
	}
	
	@GetMapping("/reActive")
	public String reActiveId(MemberDto member, HttpSession session) {
		//비활성화된 유저의 계정을 활성화시킴
		memberService.reActiveId(member, session);
		return "redirect:/main";
	}
	
	@GetMapping("/noHaveAuth")
	public String noHaveAuth() {
		//유저 계정이 활성화가 안되어있거나 비활성화가 되어있는 경우에 이동하는 페이지
		return "nohaveAuth.tiles";
	}
	@GetMapping("/authKeyId") //회원가입후에 계정 인증을 하려고 할 경우
	public String confirmId(MemberDto member,HttpSession session) {
		if(memberService.idActive(member,session)) {
			return "redirect:/main";
		}
		return "failAuthKey.tiles";
	}
	
	@ResponseBody
	@PostMapping("/socialLogin")
	public boolean socialLogin(MemberDto member, HttpSession session) {
		//소셜로그인을 하려하는경우 (+회원이아니면 회원가입까지 진행함)
		member.setUser_password(MemberUtil.makePassword(10));
		return memberService.socialLogin(member, session);
	}
	@ResponseBody
	@PostMapping("/register")  //홈페이지를 통한 회원가입시에 사용됨
	public boolean regeisterMember(MemberDto member, HttpSession session) throws Exception {
		//회원가입 실패 유무를 반환
		return memberService.regeisterMember(member, session);
	}
	@ResponseBody
	@PostMapping("/confirmId") //true== 계정이 없슴 false==계정이 있씀
	public boolean doPageLogin(MemberDto member) {
		//회원가입 email 중복
		return memberService.confirmId(member);
	}
	
	@ResponseBody
	@PostMapping("/login") //로그인정보 일치시
	public String doLogin(MemberDto member, HttpSession session) throws Exception{
		//로그인 실행         true ==정보가 있슴(성공) false는 정보가 없슴(실패)
		return memberService.doLogin(member, session);
	}
	
	@ResponseBody
	@GetMapping("/logout")
	public void logout(HttpSession session) { //로그인한 세션을 삭제
		session.removeAttribute("login");
	}
	
	@ResponseBody
	@RequestMapping(value="/getNaverLink",method= {RequestMethod.GET})
	public String getNaverLoginLink(Model model, HttpSession session) {
		//네이버 인증페이지로 이동을 위한 링크생성하는 ajax
		String naverAuthUrl = naver.getAuthorizationUrl(session);
		
		return naverAuthUrl;
	}
		
	@RequestMapping(value="/naverLogin",method= {RequestMethod.GET})
	public String naverCallBack(Model model, HttpSession session, @RequestParam String code, @RequestParam String state) throws Exception {
		//naver로 보낸요청을 다시 받아오는 부분
		
		OAuth2AccessToken oauthToken;
		oauthToken = naver.getAccessToken(session, code, state);
		
		//사용자정보 json으로 받아옴
		apiResult = naver.getUserProfile(oauthToken);
		
		//String형식을 json으로 변환 
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(apiResult);
		JSONObject jsonObj = (JSONObject)obj;
		
		//json안의 response파싱
		JSONObject responseObj = (JSONObject)jsonObj.get("response");
		String nickname = (String)responseObj.get("nickname");
		model.addAttribute("result",apiResult);
		
		return "naverLogin.tiles";
	}
	
	@GetMapping("/goLogin")
	public String goLoginPage() { //로그인서비스가 필요한 사이트 이용시에
		return "loginPage.tiles";
	}
	
	@RequestMapping(value="/loginModal",method= {RequestMethod.GET})
	public String getModal(){ //로그인 모달창 html 호출
		return "modal.tiles";
	}
	@RequestMapping(value="/loginForm",method= {RequestMethod.GET})
	public String getLoginForm(){ //로그인 폼 html 호출
		return "getLogin.tiles";
	}
	@RequestMapping(value="/regiForm",method= {RequestMethod.GET})
	public String getRegiForm(){ //회원가입 폼 html 호출
		return "getRegi.tiles";
	}
	
}
