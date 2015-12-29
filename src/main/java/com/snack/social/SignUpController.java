package com.snack.social;

import com.snack.user.User;
import com.snack.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@Controller
public class SignUpController {

	@Autowired
	private ProviderSignInUtils providerSignInUtils;

	@Autowired
	private SocialUserService socialUserService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String redirectRequestToRegistrationPage(WebRequest request, ModelMap modelMap) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		UserProfile userProfile = connection.fetchUserProfile();

		UserCreateRequestVO userCreateRequestVO = UserCreateRequestVO.fromSocialUserProfile(userProfile);

		try {
			User user = new User();
			user.setUserId(userCreateRequestVO.getEmail());
			user.setRegDate(new Date());
			user.setName(userCreateRequestVO.getLastName() + userCreateRequestVO.getFirstName());
			userService.create(user);

			SocialUser socialUser = socialUserService.create(userCreateRequestVO);
			providerSignInUtils.doPostSignUp(socialUser.getEmail(), request);

			FrontUserDetail frontUserDetail = new FrontUserDetail(socialUser);

			Authentication authentication = new UsernamePasswordAuthenticationToken(frontUserDetail, null, frontUserDetail.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			return String.format("redirect:/error?message=%s", e.getMessage());
		}

		return "redirect:/";
	}
}
