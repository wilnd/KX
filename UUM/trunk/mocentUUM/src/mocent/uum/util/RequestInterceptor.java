package mocent.uum.util;

import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mocent.uum.entity.User;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author jiang
 * @date 2016年9月29日  上午9:42:31
 *
 */
public class RequestInterceptor implements HandlerInterceptor{

	/**
	 * 该方法将在请求处理之前进行调用
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		User user = (User)request.getSession().getAttribute("currUser");
		String rebutURL = "/mocentUUM/";
		if(user == null){
			
			response.sendRedirect(rebutURL);
			return false;
		}else{
			if(!"admin".equals(user.getUsername())){
				response.sendRedirect(rebutURL);
				return false;
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}


}
