package mocent.uum.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mocent.uum.service.IMocentKXService;
import mocent.uum.util.FtpUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/mocentKX")
@Controller
public class MocentKXContorller {

	@Autowired
	private IMocentKXService mocentService;
	@ResponseBody
	@RequestMapping("/addDownTask")
	public String addDownTask(Integer kxId,String carId){
		String[] split = carId.split(",");
		List<Integer> list=new ArrayList<Integer>();
		for (String string : split) {
			if(null !=string && !"".equals(string)){
				list.add(Integer.valueOf(string));
			}
		}
		return mocentService.addDownTask(kxId, list);
	}
	/*@ResponseBody
	@RequestMapping("/test")
	public void test(){
		System.out.println("welcome");
		File file=new File("/var/lib/tomcat7/work/mocentKX/2016/12/9");
		if(!file.exists()){
			file.mkdirs();
			System.out.println("chuangjjian"+file.exists());
		}
	}*/
	
}
