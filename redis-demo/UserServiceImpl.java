

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

	/**
	 * 发送激活邮件
	 * 
	 * @param user
	 *            用户信息
	 * @param requestUrl
	 *            浏览器URL的ROOT路径
	 * @return
	 */
	public Map<String, Object> sendActivationEmail(User user, String requestUrl) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//省略业务代码
			RedisString redisString = new RedisString();
			redisString.setex(userId + "&emailActivation", 86400, activationUrl);// redis数据库中添加过期时间验证
		
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
		}
		return result;
	}
	
	/**
	 * redis 验证
	 */
	public Map<String, Object> redisVerification(String key, String value){
		Map<String, Object> result = new HashMap<String, Object>();
		RedisString redisString = new RedisString();
		if (null == redisString.get(key)) {
			result.put("success", false);
			result.put("errorCode", "user.registration.redis-connection-failure");
			result.put("errorMessage", "链接失效");
			return result;
		}
		if (value.equals(redisString.get(key))) {
			redisString.del(key);
			result.put("success", true);
			return result;
		}
		result.put("success", false);
		result.put("errorCode", "user.registration.redis-abnormal-data");
		result.put("errorMessage", "数据异常");
		return result;
	}
}
