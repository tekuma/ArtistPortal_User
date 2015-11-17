package com.pintu.system.action;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.batik.apps.rasterizer.Main;
import org.apache.catalina.connector.Request;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import sun.misc.BASE64Decoder;

import com.pintu.pub.action.BaseAction;
import com.pintu.system.entity.Code;
import com.pintu.system.entity.Collection;
import com.pintu.system.entity.Member;
import com.pintu.system.entity.Pool;
import com.pintu.system.service.SystemService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

@SuppressWarnings({ "unused", "unchecked", "serial" })
@Scope("prototype")
@Controller(value = "systemAction")
public class SystemAction extends BaseAction {
	@Resource
	private SystemService systemService;
	private Member member;// 会员
	private List<Pool> pools;// pool作品集
	private Pool pool;// 作品
	private Collection collection;// 收藏夹
	private File[] upload;//图像上传
	private String[] uploadFileName;  
	private String[] uploadContentType; 
	private String isSuccess;//是否登陆成功
	private String urlContentType;
	
	private Code codedate;//邀请码
	
	public Code getCodedate() {
		return codedate;
	}
	public void setCodedate(Code codedate) {
		this.codedate = codedate;
	}
	
	public String getUrlContentType() {
		return urlContentType;
	}
	public void setUrlContentType(String urlContentType) {
		this.urlContentType = urlContentType;
	}
	//图片压缩
    private Image img;  
    private int width;  
    private int height;  
	private String imgSuffix;//文件后缀名
	//常量
	public  static String LOCALFILEURL="";//服务器本地地址
	private final static Integer YS_WIDTH=210;//压缩图片宽度
	private final static Integer YS_HEIGHT=210;//压缩图片高度
	private final static String YX_NAME="artist@tekuma.io";//邮箱登陆名
	private final static String YX_PWD="Tekuma2015";//邮箱登陆密码
	private final static String YX_SMTP="smtpout.secureserver.net";//邮箱smtp
	/** 
     * 按照宽度还是高度进行压缩 
     * @param w int 最大宽度 
     * @param h int 最大高度 
     */  
    public void resizeFix(int w, int h) throws IOException {  
        if (width / height > w / h) {  
            resizeByWidth(w);  
        } else {  
            resizeByHeight(h);  
        }  
    }  
    /** 
     * 以宽度为基准，等比例放缩图片 
     * @param w int 新宽度 
     */  
    public void resizeByWidth(int w) throws IOException {  
        int h = (int) (height * w / width);  
        resize(w, h);  
    }  
    /** 
     * 以高度为基准，等比例缩放图片 
     * @param h int 新高度 
     */  
    public void resizeByHeight(int h) throws IOException {  
        int w = (int) (width * h / height);  
        resize(w, h);  
    }  
    
	/**
	 * 加载Pool作品
	 * 
	 * @return
	 */
	public String InitWorksIndex() {
		Member member = (Member)this.request.getSession().getAttribute("member");
		List<Pool> poolList = this.systemService.findPoolByUserId(member.getId(),0);
		pools=new ArrayList<Pool>();
		if(poolList!=null&&poolList.size()>0){
			String fileUrl=this.systemService.findFileUrl();
			for(Pool pool:poolList){
				pool.setThumbnailurl(fileUrl+pool.getStoreaddress().replace(".", "tb."));
				pool.setThumbnailurlac(fileUrl+pool.getStoreaddress().replace(".", "ac."));
				pool.setStoreaddress(fileUrl+pool.getStoreaddress());
				pools.add(pool);
			}
		}
		return "worksIndex";
	}

	// ajax加载pool
	public void ajaxInitPool() {
		Member member = (Member)this.request.getSession().getAttribute("member");
		Integer page=Integer.valueOf(this.request.getParameter("page"));
		pools = this.systemService.findPoolByUserId(member.getId(),page);
		this.listReturnJson(pools);
	}
	
	//查看作品翻页
	public void poolflip(){
		Member member = (Member)this.request.getSession().getAttribute("member");
		String type=this.request.getParameter("type");
		String flipid=this.request.getParameter("flipid");
		int id=Integer.valueOf(flipid);
		String sql="select max(id) from tk_pool_t where MemberID="+member.getId();
		String max=this.systemService.getPublicJdbcDao().getTempStr(sql, null);
		int mx=Integer.valueOf(max);
		String Sql="select min(id) from tk_pool_t where MemberID="+member.getId();
		String min=this.systemService.getPublicJdbcDao().getTempStr(Sql, null);
		int mn=Integer.valueOf(min);
		
		String SQL;
		pools=new ArrayList<Pool>();
		for(int i=1;i<mx;i++){
			if(type.equals("right")){
				if(id-i >= mn){
					SQL="select * from tk_pool_t where id="+(id-i)+" and MemberID="+member.getId();
					List<Pool> poolList = this.systemService.getPublicJdbcDao().getList(SQL, Pool.class);
					
					if(poolList!=null&&poolList.size()>0){
						String fileUrl=this.systemService.findFileUrl();
						for(Pool pool:poolList){
							pool.setThumbnailurl(fileUrl+pool.getStoreaddress().replace(".", "tb."));
							pool.setThumbnailurlac(fileUrl+pool.getStoreaddress().replace(".", "ac."));
							pool.setStoreaddress(fileUrl+pool.getStoreaddress());
							pools.add(pool);
						}
						break;
					}
				}else{
					SQL="select * from tk_pool_t where id="+mx+" and MemberID="+member.getId();
					List<Pool> poolList = this.systemService.getPublicJdbcDao().getList(SQL, Pool.class);
				
					if(poolList!=null&&poolList.size()>0){
						String fileUrl=this.systemService.findFileUrl();
						for(Pool pool:poolList){
							pool.setThumbnailurl(fileUrl+pool.getStoreaddress().replace(".", "tb."));
							pool.setThumbnailurlac(fileUrl+pool.getStoreaddress().replace(".", "ac."));
							pool.setStoreaddress(fileUrl+pool.getStoreaddress());
							pools.add(pool);
						}
						break;
					}
				}
			}else{
				if(id+i <= mx){
					SQL="select * from tk_pool_t where id="+(id+i)+" and MemberID="+member.getId();
					List<Pool> poolList = this.systemService.getPublicJdbcDao().getList(SQL, Pool.class);
					
					if(poolList!=null&&poolList.size()>0){
						String fileUrl=this.systemService.findFileUrl();
						for(Pool pool:poolList){
							pool.setThumbnailurl(fileUrl+pool.getStoreaddress().replace(".", "tb."));
							pool.setThumbnailurlac(fileUrl+pool.getStoreaddress().replace(".", "ac."));
							pool.setStoreaddress(fileUrl+pool.getStoreaddress());
							pools.add(pool);
						}
						break;
					}
				}else{
					SQL="select * from tk_pool_t where id="+mn+" and MemberID="+member.getId();
					List<Pool> poolList = this.systemService.getPublicJdbcDao().getList(SQL, Pool.class);
					if(poolList!=null&&poolList.size()>0){
						String fileUrl=this.systemService.findFileUrl();
						for(Pool pool:poolList){
							pool.setThumbnailurl(fileUrl+pool.getStoreaddress().replace(".", "tb."));
							pool.setThumbnailurlac(fileUrl+pool.getStoreaddress().replace(".", "ac."));
							pool.setStoreaddress(fileUrl+pool.getStoreaddress());
							pools.add(pool);
						}
						break;
					}
				}
			}
		}
		this.listReturnJson(pools);
	}
	
	//查询添加收藏作品时的作品列表
	public void findAddCollectionList(){
		Member member = (Member)this.request.getSession().getAttribute("member");
		this.listReturnJson(this.systemService.findAddCollectionList(member.getId()));
	}
	
	//查询修改收藏作品时的作品列表
		public void findUpdateCollectionList(){
			Member member = (Member)this.request.getSession().getAttribute("member");
			Integer collectionId=Integer.valueOf(this.request.getParameter("collectionid"));
			this.listReturnJson(this.systemService.findUpdateCollectionList(member.getId(),collectionId));
		}
	
	// 查询关系表根据收藏Id
	public void findRelevancyByCollectionId() {
		Member member = (Member)this.request.getSession().getAttribute("member");
		List<Pool> pools=this.systemService.findPoolsForAddCollection(member.getId());
		this.listReturnJson(pools);
	}


	// ajax加载collection集合
	public void ajaxInitCollections() {
		Member member = (Member)this.request.getSession().getAttribute("member");
		pools = this.systemService.findCollectionByUserId(member.getId());
		this.listReturnJson(pools);
	}

	/**
	 * 保存用户信息
	 * 
	 * @return
	 */
	public void saveMemberInfo() {
		Integer index=this.systemService.getPublicJdbcDao().getTempInt(
				"select id from tk_member_t where loginname='"+member.getLoginname()+"'", 0);
		if(index!=0){
			isSuccess="The mailbox has been registered";
		}else{
			member.setId(this.systemService.getPublicJdbcDao().getTempInt("select sys_seqnextval_f ('tk_member_t')", 0));
			this.systemService.getPublicJdbcDao().executeMYSQLDB("save","tk_member_t", member, "id");
			/*String inviteCode=this.request.getParameter("invitecode");
			this.systemService.getPublicJdbcDao().executeSQL("delete from tk_code_t where number='"+inviteCode+"'");*/
			HttpSession session = this.request.getSession(); 
			session.setAttribute("member",member);
			isSuccess="success";
		}
		this.strReturnJson(isSuccess);
	}

	//提交选中的作品到收藏夹
	public void saveCollection() {
		String poolIdStr = this.request.getParameter("poolIds");// 作品Id数组
		String isAdd=this.request.getParameter("isAdd");//是否添加收藏/修改
		String[] pooldIds = poolIdStr.split(",");																// 用逗号隔开
		boolean isId = true;
		if (isAdd.equals("yes")) {// 
			isId = false;
			collection.setId(this.systemService.getPublicJdbcDao().getTempInt(
					"select sys_seqnextval_f ('tk_collection_t')", 0));
		} else {
			collection.setId(Integer.valueOf(collection.getId()));
		}
		this.systemService.createRelevancy(collection.getId(), pooldIds, isId);// 添加pool和collection的关系
		this.systemService.collectioncreateCollection(collection, isId);// 添加收藏夹
	}

	/**
	 * 添加pool作品
	 * @throws IOException 
	 */
	public void savePoolWorks() throws IOException {
		String imgname="";
		if (pool.getId()!=null) {// 修改作品
			this.systemService.getPublicJdbcDao().executeSQL("update tk_pool_t set title='"+pool.getTitle()+
					"',EntryLabel='"+pool.getEntrylabel()+"',Description='"+pool.getDescription()+"',createtime='"+pool.getCreatetime()+"' where id="+pool.getId());
		} else {// 添加作品
			imgSuffix=uploadContentType[0].split("/")[1];
			Member member=(Member)this.request.getSession().getAttribute("member");
			pool.setMemberid(member.getId());
			pool.setId(this.systemService.getPublicJdbcDao().getTempInt("select sys_seqnextval_f ('tk_pool_t')", 0));
			pool.setStoreaddress(member.getId()+"/"+pool.getId()+"."+imgSuffix);
			String sql="select max(psort) from tk_pool_t";
			int psort=this.systemService.getPublicJdbcDao().getTempInt(sql, 0);
			pool.setPsort(psort+1);
			
			for(int i=0;i<uploadFileName.length;i++){
				imgname=uploadFileName[i];
			}
			String name=imgname.substring(0,imgname.indexOf("."));
			if(pool.getTitle().length()>0){
				pool.setTitle(pool.getTitle());
			}else{
				pool.setTitle(name);
			}
			this.systemService.getPublicJdbcDao().executeMYSQLDB("save","tk_pool_t", pool, "id");
			try {
				String filePath=LOCALFILEURL+member.getId()+"/"+pool.getId()+"."+imgSuffix;
				File file = new File(filePath);
				File path=new File(LOCALFILEURL+member.getId());
				if(!path.exists()){    
					path.mkdir();    
				} 
				copy(upload[0], file);
				FileInputStream is = null;  
		        ImageInputStream iis = null; 
		        
		        
		        
		        // 读取图片文件  
		        is = new FileInputStream(upload[0]);  
		        Iterator<ImageReader> it = ImageIO  
		                .getImageReadersByFormatName(imgSuffix);  
		        ImageReader reader = it.next();  
		        // 获取图片流  
		        iis = ImageIO.createImageInputStream(is);  
		        reader.setInput(iis, true);  
		        int imgWidth=reader.getWidth(0);
		        int imgHeight=reader.getHeight(0);
		        int cutX=0;
		        int cutY=0;
		        int cutWidth=0;
		        int cutHeight=0;
		        if(imgWidth>imgHeight){
		        	cutHeight=imgHeight;
		        	cutX=(imgWidth-imgHeight)/2;
		        	cutWidth=imgHeight;
		        }else{
		        	cutWidth=imgWidth;
		        	cutY=(imgHeight-imgWidth)/2;
		        	cutHeight=imgWidth;
		        }
		        ImageReadParam param = reader.getDefaultReadParam();  
		        Rectangle rect = new Rectangle(cutX,cutY,cutWidth,cutHeight);  
		        // 提供一个 BufferedImage，将其用作解码像素数据的目标。  
		        param.setSourceRegion(rect);  
		        BufferedImage bi = reader.read(0, param);  
		        
		        // 保存新图片  
		        String cutFilePath=LOCALFILEURL+member.getId()+"/"+pool.getId()+"tb."+imgSuffix;
		        ImageIO.write(bi, imgSuffix, new File(cutFilePath));
		        
		        //保存用于作品展示的压缩图
		        String cutFilePathac=LOCALFILEURL+member.getId()+"/"+pool.getId()+"ac."+imgSuffix;
		        ImageIO.write(bi, imgSuffix, new File(cutFilePathac));
		        
		        is.close();
		        iis.close(); 
		        
		       /* BufferedImage image = new BufferedImage(YS_WIDTH, YS_HEIGHT,BufferedImage.TYPE_INT_RGB );
		        image.getGraphics().drawImage(bi, 0, 0, YS_WIDTH, YS_HEIGHT, null); // 绘制缩小后的图  
		        File destFile = new File(cutFilePath);  
		        FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流  
		        // 可以正常实现bmp、png、gif转jpg  
		        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
		        encoder.encode(bi); // JPEG编码  
		        out.close();  */
		        
		        //poll显示的缩略图
		        //createThumbnail(new File(cutFilePath));
		        //作品查看时的缩略图
		        //createThumbnailac(new File(cutFilePathac));
		        
		        SystemAction narrowImage = new SystemAction(); 
		        //poll显示的缩略图
		        //narrowImage.writeHighQuality(narrowImage.zoomImage(filePath,"tb"), cutFilePath);  
		        //作品查看时的缩略图
		        narrowImage.writeHighQuality(narrowImage.zoomImage(filePath,"ac"), cutFilePathac);  
			} catch (IOException e) {
				pool.setIsSuccess("error");
			} 
		}
		this.entityReturnJson(pool);
	}
	
	 /** 
     * 强制压缩/放大图片到固定的大小 
     * @param w int 新宽度 
     * @param h int 新高度 
     */  
    public void resize(int w, int h) throws IOException {  
    	Member member = (Member)this.request.getSession().getAttribute("member");
        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢  
        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB );   
        image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图  
        String file=LOCALFILEURL+member.getId()+"/"+pool.getId()+"tb"+"."+imgSuffix;
        File destFile = new File(file);  
        FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流  
        // 可以正常实现bmp、png、gif转jpg  
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
        encoder.encode(image); // JPEG编码  
        out.close();  
    }  
	
	/**
	 * 上传用户头像
	 */
	public void saveUserHead(){
		Member member=(Member)this.request.getSession().getAttribute("member");
		String imgStr=this.request.getParameter("imgUrl");
        try { 
        	String imgFilePath=saveHeadPic(imgStr,member.getId()).get("localUrl");
            FileInputStream is = null;  
            ImageInputStream iis = null;  
            // 读取图片文件  
            is = new FileInputStream(imgFilePath);  
            String [] imgStrs=imgStr.split(",");
            imgStr=imgStrs[1];
    		String imgSuffix = imgStrs[0].split("/")[1].split(";")[0];
            Iterator<ImageReader> it = ImageIO  
                    .getImageReadersByFormatName(imgSuffix);  
            ImageReader reader = it.next();  
            // 获取图片流  
            iis = ImageIO.createImageInputStream(is);  
            reader.setInput(iis, true);  
            int v=reader.getWidth(0);
            ImageReadParam param = reader.getDefaultReadParam();  
            Rectangle rect = new Rectangle(Integer.valueOf(this.request.getParameter("imgX1")),
            		Integer.valueOf(this.request.getParameter("imgY1")),
            		Integer.valueOf(this.request.getParameter("imgW")),
            		Integer.valueOf(this.request.getParameter("imgH")));  
            // 提供一个 BufferedImage，将其用作解码像素数据的目标。  
            param.setSourceRegion(rect);  
            BufferedImage bi = reader.read(0, param);  
            // 保存新图片  
            ImageIO.write(bi, imgSuffix, new File(imgFilePath));  
            is.close();
            iis.close();  
        } catch (Exception e) {
			this.strReturnJson("error");
		}
	}
	
	//保存头像图片
	public Map<String, String> saveHeadPic(String img,Integer userId) throws IOException{
		String imgFilePath="";
		String [] imgStrs=img.split(",");
		img=imgStrs[1];
		String imgSuffix = imgStrs[0].split("/")[1].split(";")[0];
        BASE64Decoder decoder = new BASE64Decoder();  
            //Base64解码  
            byte[] b = decoder.decodeBuffer(img);  
            for(int i=0;i<b.length;++i){  
                if(b[i]<0){//调整异常数据  
                    b[i]+=256;  
                }  
            }  
            //生成jpeg图片  
            imgFilePath = LOCALFILEURL+userId+"/head/"+userId+"."+imgSuffix;//新生成的图片  
            
            File file=new File(LOCALFILEURL+userId);
            if(!file.exists()){    
                file.mkdir();
            } 
            file=new File(LOCALFILEURL+userId+"/head");
            if(!file.exists()){
            	file.mkdir();
            }
            
            OutputStream out = new FileOutputStream(imgFilePath);      
            out.write(b); 
            out.flush();  
            out.close();
            String headUrl=userId+"/head/"+userId+"."+imgSuffix;
            String sql="update tk_member_t set AvatarPath='"+userId+"/head/"+userId+"."+imgSuffix+"' where id="+userId;
            Map<String, String>map=new HashMap<String, String>();
            map.put("localUrl", imgFilePath);
            map.put("serverUrl", headUrl);
            this.systemService.getPublicJdbcDao().executeSQL(sql);
        return map;
	}
	
	/** 
     * 构造函数 
     */  
    public void createThumbnail(File file) throws IOException {  
    	img = ImageIO.read(file);      // 构造Image对象  
        width = img.getWidth(null);    // 得到源图宽  
        height = img.getHeight(null);  // 得到源图长  
        resizeFix(YS_WIDTH,YS_HEIGHT);
    } 
    
    /**
     * 压缩为查看作品时显示的图片
     * @param file
     * @throws IOException
     *//*
    public void createThumbnailac(File file) throws IOException {  
    	 img = ImageIO.read(file);      // 构造Image对象  
  
    	 width = img.getWidth(null);    // 原图宽度 
         height = img.getHeight(null);  // 原图高度
         
        resize(width,height);
    } 
    */
	/**
	 * 复制文件
	 * 
	 * @param src
	 * @param dst
	 */
	private void copy(File src, File dst) {
		InputStream in = null;
		OutputStream out = null;
		try {
			int BUFFER_SIZE = 100;
			out = new BufferedOutputStream(new FileOutputStream(dst),BUFFER_SIZE);
			in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Java文件操作 获取文件扩展名
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}
	
	/**
	 * 登陆
	 * @return
	 */
	public void memberLogin(){
		String isSuccess="";
		Integer userIndex=this.systemService.getPublicJdbcDao().getTempInt(
				"select id from tk_member_t where LoginName='"+member.getLoginname()+"'", 0);
		if(userIndex!=0){
			List<Member> members=this.systemService.getPublicJdbcDao().getList(
					"select id,loginname,loginpwd,lastname,firstname,birthday,telephone,email,avatarpath,introduction,membertype,createtime,bank,bankaccount,payee,location,nationality,bio,website,gender"
					+ " from tk_member_t where LoginName='"+member.getLoginname()+"'"+
					" and LoginPwd='"+member.getLoginpwd()+"'", Member.class);
			if(members!=null&&members.size()>0){
				isSuccess="success###"+members.get(0).getId();
				HttpSession session = this.request.getSession(); 
				session.setAttribute("member",members.get(0));
				
				//用户名密码保存到cookie一年
				Cookie cookieuser = new Cookie("user",member.getLoginname()+"-"+member.getLoginpwd()); 
				cookieuser.setMaxAge(365 * 24 * 60 * 60); 
				response.addCookie(cookieuser); 

			}else{
				isSuccess="2";
			}	
		}else{
			isSuccess="1";
		}
		this.strReturnJson(isSuccess);
	}
	
	public void MemberFBLogin(){
		Integer index=this.systemService.getPublicJdbcDao().getTempInt(
				"select count(fbid) from tk_member_t where fbid='"+member.getFbid()+"'", 0);
		if(index==0){
			member.setAvatarpath("graph.facebook.com/"+member.getFbid()+"/picture");
			this.systemService.getPublicJdbcDao().executeMYSQLDB("save", "tk_member_t", member, null);
		}
		System.out.print(member);
		HttpSession session = this.request.getSession(); 
		session.setAttribute("member",member);
		
	}
	
	/**
	 * 发送邮件
	 * @throws ServletException
	 * @throws IOException
	 */
	public void sendMail() throws ServletException, IOException{
        String toMail = request.getParameter("email");  
        String registerName = request.getParameter("userName");  
        String registCode=this.request.getParameter("registCode");
        String type=this.request.getParameter("type");
        	boolean flag=false;
        	if(type.equals("1")){//注册
        		if(this.systemService.verifyRgistCode(registCode)){
        			flag=this.systemService.isExistMemberById(toMail);
        		}else{
        			this.strReturnJson("1");
        			return;
        		}
        	}else{//重置密码
        		flag=!this.systemService.isExistMemberById(toMail);
        	}
        	
        	if(!flag){
            	String userName = YX_NAME;  
                String password = YX_PWD;  
                Random random = new Random();
                String registerCode = String.valueOf(Math.abs(random.nextInt(999999)));
                Properties props = new Properties();   
                props.setProperty("mail.smtp.host", YX_SMTP);  
                props.setProperty("mail.smtp.auth", "true");  
                javax.mail.Authenticator authenticator = new MyAuthenticator(userName, password);  
                javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,authenticator);  
                session.setDebug(true);  
                try{  
                    InternetAddress from = new InternetAddress(userName);  
                    InternetAddress to = new InternetAddress(toMail);  
                    MimeMessage msg = new MimeMessage(session);  
                    msg.setFrom(from);  
                    msg.setSubject("TAKUMA");  
                    msg.setSentDate(new Date());  
                    msg.setContent("Your registration code is:" + registerCode+",Please enter the registration code to complete the registration page.", "text/html;charset=utf-8");  
                    msg.setRecipient(RecipientType.TO, to);  
                    Transport transport = session.getTransport("smtp"); 
                    transport.connect(YX_SMTP, userName, password); 
                    transport.sendMessage(msg,msg.getAllRecipients()); 
                    transport.close(); 
                    //Transport.send(msg);  
                } catch(MessagingException e){  
                    e.printStackTrace();  
                }  
                this.strReturnJson(registerCode);
            }else{
            	this.strReturnJson("2");
            }
	}
	
	
	//验证邀请码与邮箱是否对应
	public void registCode(){
		String registCode=this.request.getParameter("singupInvitationcode");
		this.codedate=this.systemService.verifyRgistCodeconfig(registCode);
		this.entityReturnJson(this.codedate);
	}
	
	
	//修改密码
	public void saveResetPassword(){
		String pwd=this.request.getParameter("pwd");
		String loginName=this.request.getParameter("loginName");
		this.systemService.getPublicJdbcDao().executeSQL("update tk_member_t set loginpwd='"+pwd+"' where loginname='"+loginName+"'");
	}
	
	//保存用户设置
	public void saveSetup(){
		Member member=(Member)this.request.getSession().getAttribute("member");
		String oldpwd=this.request.getParameter("oldpwd");
		String loginName=member.getLoginname();
		String sql="select id from tk_member_t where loginName='"+loginName+"' and loginPwd='"+oldpwd+"'";
		Integer index=this.getSystemService().getPublicJdbcDao().getTempInt(sql, 0);
		if(index==0){
			this.strReturnJson("1");
		}else{
			String newpwd=this.request.getParameter("newpwd");
			sql="update tk_member_t set loginPwd='"+newpwd+"' where loginName='"+loginName+"'";
			this.systemService.getPublicJdbcDao().executeSQL(sql);
			
		}
		
	}
	
	/**
	 * 完善用户信息
	 */
	public void updateUserInfo(){
		String img=this.request.getParameter("headImg");
		Member sessionMember=(Member)this.request.getSession().getAttribute("member");
		String avatarPath="";
		if(img.indexOf("http://")==-1){
			try {
				String Sql="select avatarpath from tk_member_t where ID="+sessionMember.getId();
				String txlj=this.systemService.getPublicJdbcDao().getTempStr(Sql, null);
				String filePath=LOCALFILEURL+txlj;
				new File(filePath).delete();
				
				avatarPath=saveHeadPic(img,sessionMember.getId()).get("serverUrl");
			} catch (IOException e) {
				member.setIsSuccess("1");
			}
		}else{
			avatarPath=sessionMember.getAvatarpath();
		}
		member.setAvatarpath(avatarPath);
		String sql="update tk_member_t set firstName='"+member.getFirstname()+"', lastName='"+member.getLastname()+
				"',location='"+member.getLocation()+"',bio='"+member.getBio()+"',website='"+member.getWebsite()+
				"',gender='"+member.getGender()+"' where id="+sessionMember.getId();
		this.systemService.getPublicJdbcDao().executeSQL(sql);
		this.entityReturnJson(member);
	}
	
	/**
	 * 查询文件服务器地址
	 */
	public void getServerFileUrl(){
		String sql="select csz from sys_param_t where bh=1";
		List<String> list=this.systemService.getPublicJdbcDao().getList(sql);
		String Sql="select csz from sys_param_t where bh=2";
		List<String> listdz=this.systemService.getPublicJdbcDao().getList(Sql);
		LOCALFILEURL=listdz.get(0);
		this.strReturnJson(list.get(0));
	}
	
	/**
	 * 查询头像地址
	 */
	public void getServerusertxlj(){
		Member sessionMember=(Member)this.request.getSession().getAttribute("member");
		String Sql="select * from tk_member_t where ID="+sessionMember.getId();
		List<Member> members=this.systemService.getPublicJdbcDao().getList(Sql, Member.class);
		this.listReturnJson(members);
	}
	
	
	/**
	 * 删除pool根据id
	 */
	public void delPoolById(){
		
		//删除图片文件
		Member sessionMember=(Member)this.request.getSession().getAttribute("member");
		String Sql="select storeaddress from tk_pool_t where ID="+pool.getId();
		String lj=this.systemService.getPublicJdbcDao().getTempStr(Sql, null);
		String filePath=LOCALFILEURL+lj;
		String cutFilePathtb=LOCALFILEURL+lj.replace(".", "tb.");
		String cutFilePathac=LOCALFILEURL+lj.replace(".", "ac.");
		new File(filePath).delete(); 
		new File(cutFilePathtb).delete(); 
		new File(cutFilePathac).delete(); 
		//清除数据库
		String sql="delete from tk_pool_t where id="+pool.getId();
		this.systemService.getPublicJdbcDao().executeSQL(sql);
		sql="delete from tk_relevancy_t where poolId="+pool.getId();
		this.systemService.getPublicJdbcDao().executeSQL(sql);
		
	}
	
	/**
	 * 删除收藏夹根据id
	 */
	public void delCollectionById(){
		String sql="delete from tk_collection_t where id="+collection.getId();
		this.systemService.getPublicJdbcDao().executeSQL(sql);
		sql="delete from tk_relevancy_t where collectionId="+collection.getId();
		this.systemService.getPublicJdbcDao().executeSQL(sql);
	}
	
	
	
	
	
	/*==================hg======================*/
	
	/**
     * hg
	 * 退出系统
	 * @return
	 * */
	public String exitIndex(){
		this.request.getSession().removeAttribute("member");
		return "login";
	}

	//远程图片下载(单文件)
    public  void download()throws Exception {
    		 // 构造URL
    		String urlString=this.request.getParameter("urlString");
    		String filename=this.request.getParameter("filename");
    		int index = filename.lastIndexOf("/")+1;
    		//String left = filename.substring(0,index);
    		String right = filename.substring(index);
    		/*String fname="E://tp/";
    		File name=new File(fname+left);
    		if(!name.exists()){
    			name.mkdir();
    		}*/ 
    		OutputStream outs = response.getOutputStream();// 获取文件输出IO流  
            BufferedOutputStream bouts = new BufferedOutputStream(outs);  
            response.setContentType("application/x-download");// 设置response内容的类型  
            response.setHeader(  
                    "Content-disposition",  
                    "attachment;filename="  
                            + URLEncoder.encode(right, "UTF-8"));
  
    		  URL url = new URL(urlString);
    		  // 打开连接
    		  HttpURLConnection con = (HttpURLConnection) url.openConnection();
    		  //URLConnection con = url.openConnection();
    		  con.setDoInput(true);//设置是否要从 URL 连接读取数据,默认为true  
    		  con.connect();  
    		  // 输入流
    		  InputStream is = con.getInputStream();
    		  String code=con.getHeaderField("Content-Encoding");
    		  //System.out.println("cdoe:"+code);
    		  if ((null!=code)&& code.equals("gzip"))
    		  {
    		   GZIPInputStream gis = new GZIPInputStream(is);
    		   // 1K的数据缓冲
    		   byte[] bs = new byte[8192];
    		   // 读取到的数据长度
    		   int len;
    		   // 输出的文件流
    		   //OutputStream os = new FileOutputStream(fname+filename);
    		   // 开始读取
    		   while ((len = gis.read(bs)) != -1) {
    		    //os.write(bs, 0, len);
    		    bouts.write(bs, 0, len);  
    		   }
    		   // 完毕，关闭所有链接
    		   bouts.flush();
    		   gis.close();
    		   //os.close();
    		   is.close();
    		  }
    		  else
    		  {
    		   // 1K的数据缓冲
    		   byte[] bs = new byte[8192];
    		   // 读取到的数据长度
    		   int len;
    		   // 输出的文件流
    		   //OutputStream os = new FileOutputStream(fname+filename);
    		   // 开始读取
    		   while ((len = is.read(bs)) != -1) {
    		    //os.write(bs, 0, len);
    		    bouts.write(bs, 0, len);  
    		   }
    		   // 完毕，关闭所有链接
    		   bouts.flush();
    		   //os.close();
    		   is.close();
    		  }
    }
	
    /**
	 * 自动登陆跳转登陆页面
	 * @return
	 */
	public void getCookes(){
		
		String data = ""; //用户名 密码
		Cookie cookies[] = request.getCookies();
		if(cookies != null){
			for(int i=0;i<cookies.length;i++){
				if(cookies[i].getName().equals("user")){  
					data+=cookies[i].getValue().split("-")[0]+":"; 
					data+=cookies[i].getValue().split("-")[1]; 
				}
			}
		}
		this.entityReturnJson(data);
	}
	
	
	
	
	
	//整体压缩图片开始
	public BufferedImage zoomImage(String src,String type) {  
        BufferedImage result = null;  
        try {  
            File srcfile = new File(src);  
            if (!srcfile.exists()) {    
            }  
            BufferedImage im = ImageIO.read(srcfile); 
        	//原始图像的宽度和高度   
        	double width = im.getWidth();  
            double height = im.getHeight();  
            int widthy = im.getWidth();  
            int heighty = im.getHeight();
            int toWidth;
            int toHeight;
            double scale;
            String ratio;
            float resizeTimes;
            if(type.equals("tb")){
            	 if(width>height){
                	 scale=width / height;
                	 ratio=""+scale+"f";
                     //压缩计算  
                     resizeTimes=Float.parseFloat(ratio);  //这个参数是要转化成的倍数,如果是1就是转化成1倍   
                     //调整后的图片的宽度和高度   
                     toWidth = (int) (210 * resizeTimes);  
                     toHeight = (int) (210); 
               	 }else{
                	 scale=height / width;
                	 ratio=""+scale+"f";
                     //压缩计算  
                     resizeTimes=Float.parseFloat(ratio);  //这个参数是要转化成的倍数,如果是1就是转化成1倍   
                     //调整后的图片的宽度和高度   
                     toWidth = (int) (210);  
                     toHeight = (int) (210 * resizeTimes); 
                 }
            }else{
            	if(width>900||height>700){
            		if(width>height){
	                	scale=YS_WIDTH*2.1 / height;
	                }else{
	                	scale=YS_HEIGHT*2.1 / width;
	                }
	                ratio=""+scale+"f";
	                //压缩计算  
	                resizeTimes =Float.parseFloat(ratio);  //这个参数是要转化成的倍数,如果是1就是转化成1倍   
	                //调整后的图片的宽度和高度   
	                toWidth = (int) (width * resizeTimes);  
	                toHeight = (int) (height * resizeTimes); 
            	}else{
            		toWidth = widthy;  
 	                toHeight = heighty;
            	}
            }

            /* 新生成结果图片 */  
            result = new BufferedImage(toWidth, toHeight,  
                    BufferedImage.TYPE_INT_RGB);  
  
            result.getGraphics().drawImage(  
                    im.getScaledInstance(toWidth, toHeight,  
                            java.awt.Image.SCALE_SMOOTH), 0, 0, null);  
        } catch (Exception e) {  
            System.out.println("创建缩略图发生异常" + e.getMessage());  
        }  
        return result;  
    }  
      
     public boolean writeHighQuality(BufferedImage im, String fileFullPath) {  
            try {  
                /*输出到文件流*/  
                FileOutputStream newimage = new FileOutputStream(fileFullPath);  
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);  
                JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(im);  
                /* 压缩质量 */  
                jep.setQuality(0.9f, true);  
                encoder.encode(im, jep);  
               /*近JPEG编码*/  
                newimage.close();  
                return true;  
            } catch (Exception e) {  
                return false;  
            }  
        }  
   //整体压缩图片结束
	
	/*========================================*/

	/**
	 * 跳转登陆页面
	 * @return
	 */
	public String goLogin(){
		
		String name = ""; //用户名
		String passward = ""; //密码
		Cookie cookies[] = request.getCookies();
		if(cookies != null){
			for(int i=0;i<cookies.length;i++){
				if(cookies[i].getName().equals("user")){  
					name=cookies[i].getValue().split("-")[0]; 
					passward=cookies[i].getValue().split("-")[1]; 
					request.setAttribute("name",name);  //存用户名
					request.setAttribute("pass",passward); //存密码
				}
			}
		}
		return "login";
	}

	/**
	 * 跳转注册信息完善页面
	 * @return
	 */
	public String goSignup(){
		String email=this.request.getParameter("email");
		String password=this.request.getParameter("password");
		request.getSession().setAttribute("email", email);
		request.getSession().setAttribute("password", password);
		return "signup";
	}
	
	/**
	 * 跳转找回密码页面
	 * @return
	 */
	public String goReset(){
		return "reset";
	}
	
	/**
	 * 收藏夹查看页-作品拖拽排序
	 */
	public void collictionpoolSort(){
		String id=this.request.getParameter("sortid");
		String px=this.request.getParameter("sort");
		String[] sortid; 
		String[] sortpx; 
		sortid=id.split(",");
		sortpx=px.split(",");
		//string[]数组转为int[]
		int[] intpx=new int[sortpx.length];
		for(int j=0;j<sortpx.length;j++){
			intpx[j]=Integer.parseInt(sortpx[j]);
		}
		Arrays.sort(intpx);//按数字升序进行排序。
		//System.out.println("添加到数据库的顺序");
		for(int i=0;i<sortid.length;i++){
			//System.out.println(sortid[i]+"+"+intpx[i]);
			String sql="update tk_pool_t set psort='"+intpx[i]+"' where ID="+sortid[i];
			this.systemService.getPublicJdbcDao().executeSQL(sql);
		}
	}
	
	
	/**
	 * 跳转上传头像页面
	 * @return
	 */
	public String goCutAvatar(){
		return "cutavatar";
	}
	
	public Image getImg() {
		return img;
	}
	public void setImg(Image img) {
		this.img = img;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String[] getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String[] uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}

	public File[] getUpload() {
		return upload;
	}

	public void setUpload(File[] upload) {
		this.upload = upload;
	}

	public String[] getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String[] uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public Pool getPool() {
		return pool;
	}

	public void setPool(Pool pool) {
		this.pool = pool;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Pool> getPools() {
		return pools;
	}

	public void setPools(List<Pool> pools) {
		this.pools = pools;
	}

	public SystemService getSystemService() {
		return systemService;
	}
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}
	public String getImgSuffix() {
		return imgSuffix;
	}
	public void setImgSuffix(String imgSuffix) {
		this.imgSuffix = imgSuffix;
	}
}

class MyAuthenticator extends Authenticator {  
    private String userName;  
    private String password;  
  
    public MyAuthenticator(String userName, String password){  
        this.userName = userName;  
        this.password = password;  
    }  
  
    @Override  
    protected PasswordAuthentication getPasswordAuthentication() {  
        return new PasswordAuthentication(userName, password);  
    }  
    
    
    
}
