package com.snack.social;

import com.snack.user.User;
import com.snack.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
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

	@Autowired
	ConnectionRepository connectionRepository;

	@Autowired
	private UserConnectionRepository userConnectionRepository;

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(WebRequest request, ModelMap modelMap) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		UserProfile userProfile = connection.fetchUserProfile();

		UserProfileDto userProfileDto = UserProfileDto.fromUserProfile(userProfile);

		try {
			// User
			User user = new User();
			user.setUserId(userProfileDto.getEmail());
			user.setRegDate(new Date());
			user.setName(userProfileDto.getName());
			userService.create(user);

			// UserConnection
			SocialUser socialUser = socialUserService.create(userProfileDto);
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
