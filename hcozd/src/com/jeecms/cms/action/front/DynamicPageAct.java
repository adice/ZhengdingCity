package com.jeecms.cms.action.front;

import static com.jeecms.common.web.Constants.INDEX;
import static com.jeecms.common.web.Constants.INDEX_HTML;
import static com.jeecms.common.web.Constants.INDEX_HTML_MOBILE;
import static  com.jeecms.cms.Constants.TPLDIR_INDEX;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentCheck;
import com.jeecms.cms.manager.assist.CmsKeywordMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentBuyMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.page.Paginable;
import com.jeecms.common.page.SimplePage;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsGroup;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.jeecms.core.web.util.URLHelper;
import com.jeecms.core.web.util.URLHelper.PageInfo;

@Controller
public class DynamicPageAct {
	private static final Logger log = LoggerFactory
			.getLogger(DynamicPageAct.class);
	/**
	 * 首页模板名称
	 */
	public static final String TPL_INDEX = "tpl.index";
	public static final String GROUP_FORBIDDEN = "login.groupAccessForbidden";
	public static final String CONTENT_STATUS_FORBIDDEN ="content.notChecked";
	
	public String convert(String utfString){  
	    StringBuilder sb = new StringBuilder();  
	    int i = -1;  
	    int pos = 0;  
	      
	    while((i=utfString.indexOf("\\u", pos)) != -1){  
	        sb.append(utfString.substring(pos, i));  
	        if(i+5 < utfString.length()){  
	            pos = i+6;  
	            sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
	        }  
	    }  
	      
	    return sb.toString();  
	}  

	/**
	 * TOMCAT的默认路径
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		//使用静态首页而且静态首页存在
		if(existIndexPage(site)){
			return goToIndexPage(request, response, site);
		}else{
			String tpl = site.getTplIndex();
			if (!StringUtils.isBlank(tpl)) {
				return tpl;
			} else {
				return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_INDEX, TPL_INDEX);
			}
		}
	}
	

	/**
	 * WEBLOGIC的默认路径
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index.jhtml", method = RequestMethod.GET)
	public String indexForWeblogic(HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		return index(request, response,model);
	}

	/**
	 * 动态页入口
	 */
	@RequestMapping(value = "/**/*.*", method = RequestMethod.GET)
	public String dynamic(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		// 尽量不要携带太多参数，多使用标签获取数据。
		// 目前已知的需要携带翻页信息。
		// 获得页号和翻页信息吧。
		int pageNo = URLHelper.getPageNo(request);
		String[] params = URLHelper.getParams(request);
		PageInfo info = URLHelper.getPageInfo(request);
		String[] paths = URLHelper.getPaths(request);
		
		String culContentType=request.getParameter("culContentType");
		if(culContentType==null || "".equals(culContentType)){
			culContentType = "1,2,3,4";
		}
		model.addAttribute("culContentType", culContentType);
		String channelId=request.getParameter("channelId");
		if(channelId==null || "".equals(channelId)){
			channelId = "112";
		}
		model.addAttribute("channelId", channelId);
		//如果是112则查询图书
		if("112".equals(channelId)){
			String bookName=request.getParameter("bookName")!=null?request.getParameter("bookName"):"";
			String authorName=request.getParameter("authorName")!=null?request.getParameter("authorName"):"";
			String publishreignPeriod=request.getParameter("publishreignPeriod")!=null?request.getParameter("publishreignPeriod"):"";
			String publisher=request.getParameter("publisher")!=null?request.getParameter("publisher"):"";
			model.addAttribute("bookName",bookName);
			model.addAttribute("authorName",authorName);
			model.addAttribute("publishreignPeriod", publishreignPeriod);
			model.addAttribute("publisher", publisher);
		}else if("118".equals(channelId)){
			String rubbingTitle=request.getParameter("rubbingTitle")!=null?request.getParameter("rubbingTitle"):"";
			String authorName=request.getParameter("authorName")!=null?request.getParameter("authorName"):"";
			String authorDynasty=request.getParameter("authorDynasty")!=null?request.getParameter("authorDynasty"):"";
			model.addAttribute("rubbingTitle",rubbingTitle);
			model.addAttribute("authorName",authorName);
			model.addAttribute("authorDynasty",authorDynasty);
		}else if("117".equals(channelId)){
			String pictureTime=request.getParameter("pictureTime")!=null?request.getParameter("pictureTime"):"";
			String picturePlace=request.getParameter("picturePlace")!=null?request.getParameter("picturePlace"):"";
			String pictureTitle=request.getParameter("pictureTitle")!=null?request.getParameter("pictureTitle"):"";
			model.addAttribute("pictureTime",pictureTime);
			model.addAttribute("picturePlace",picturePlace);
			model.addAttribute("pictureTitle",pictureTitle);
		}else if("115".equals(channelId)){
			String videoTime=request.getParameter("videoTime")!=null?request.getParameter("videoTime"):"";
			String videoPlace=request.getParameter("videoPlace")!=null?request.getParameter("videoPlace"):"";
			String videoTitle=request.getParameter("videoTitle")!=null?request.getParameter("videoTitle"):"";
			model.addAttribute("videoTime",videoTime);
			model.addAttribute("videoPlace",videoPlace);
			model.addAttribute("videoTitle",videoTitle);
		}else if("116".equals(channelId)){
			String audioTime=request.getParameter("audioTime")!=null?request.getParameter("audioTime"):"";
			String audioPlace=request.getParameter("audioPlace")!=null?request.getParameter("audioPlace"):"";
			String audioTitle=request.getParameter("audioTitle")!=null?request.getParameter("audioTitle"):"";
			model.addAttribute("audioTime",audioTime);
			model.addAttribute("audioPlace",audioPlace);
			model.addAttribute("audioTitle",audioTitle);
		}else if("113".equals(channelId)){
			String allsceneTitle=request.getParameter("allsceneTitle")!=null?request.getParameter("allsceneTitle"):"";
			model.addAttribute("allsceneTitle",allsceneTitle);
		}else if("114".equals(channelId)){
			String title=request.getParameter("title")!=null?request.getParameter("title"):"";
			model.addAttribute("title",title);
		}else if("121".equals(channelId)){
			String journalName=request.getParameter("journalName")!=null?request.getParameter("journalName"):"";
			model.addAttribute("journalName",journalName);
		}else if("122".equals(channelId)){
			String archiveName=request.getParameter("archiveName")!=null?request.getParameter("archiveName"):"";
			model.addAttribute("archiveName",archiveName);
		}else if("123".equals(channelId)){
			String mapName=request.getParameter("mapName")!=null?request.getParameter("mapName"):"";
			model.addAttribute("mapName",mapName);
		}else if("124".equals(channelId)){
			String researchName=request.getParameter("researchName")!=null?request.getParameter("researchName"):"";
			model.addAttribute("researchName",researchName);
		}else if("120".equals(channelId)){
			String title=request.getParameter("title")!=null?request.getParameter("title"):"";
			model.addAttribute("title",title);
			model.addAttribute("channelIds", "113,115,116,114");
		}else if("119".equals(channelId)){
			String isSearch=request.getParameter("isSearch");
			String []showChannelIds=null;
			if(isSearch==null || isSearch.equals("") || isSearch.equals("false")){
				model.addAttribute("isSearch", "false");
				showChannelIds=new String[]{""};
			}
			else{
				String channelIds=request.getParameter("channelIds");
				showChannelIds=channelIds.split(",");
				model.addAttribute("channelIds", channelIds);
				model.addAttribute("isSearch", "true");
			}
			String searchType=request.getParameter("searchType")!=null?request.getParameter("searchType"):"1";
			model.addAttribute("searchType",searchType);
			String showChannelId=request.getParameter("showChannelId")!=""?request.getParameter("showChannelId"):showChannelIds[0];
			if(showChannelId!=null && showChannelId.equals("120"))
				showChannelId = "113,114,115,116";
			model.addAttribute("showChannelId",showChannelId);
			String title=request.getParameter("title")!=null?request.getParameter("title"):"";
			model.addAttribute("title",title);
			String authorName=request.getParameter("authorName")!=null?request.getParameter("authorName"):"";
			model.addAttribute("authorName",authorName);
			String authorLogic=request.getParameter("authorLogic")!=null?request.getParameter("authorLogic"):"";
			model.addAttribute("authorLogic",authorLogic);
			
			String publishreignPeriod=request.getParameter("publishreignPeriod")!=null?request.getParameter("publishreignPeriod"):"";
			model.addAttribute("publishreignPeriod",publishreignPeriod);
			String publisher=request.getParameter("publisher")!=null?request.getParameter("publisher"):"";
			model.addAttribute("publisher",publisher);
			String stime=request.getParameter("stime")!=null?request.getParameter("stime"):"";
			model.addAttribute("stime",stime);
			String splace=request.getParameter("splace")!=null?request.getParameter("splace"):"";
			model.addAttribute("splace",splace);
		}
		int len = paths.length;
		if (len == 1) {
			// 单页
			return channel(paths[0],true, pageNo, params, info, request, response,
					model);
		} else if (len == 2) {
			if (paths[1].equals(INDEX)) {
				// 栏目页
				return channel(paths[0],false, pageNo, params, info, request,
						response, model);
			} else {
				// 内容页
				try {
					Integer id = Integer.parseInt(paths[1]);
					return content(id, pageNo, params, info, request, response,
							model);
				} catch (NumberFormatException e) {
					log.debug("Content id must String: {}", paths[1]);
					return FrontUtils.pageNotFound(request, response, model);
				}
			}
		} else {
			log.debug("Illegal path length: {}, paths: {}", len, paths);
			return FrontUtils.pageNotFound(request, response, model);
		}
	}
	

	public String channel(String path,boolean checkAlone, int pageNo, String[] params,
			PageInfo info, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		Channel channel = channelMng.findByPathForTag(path, site.getId());
		if (channel == null) {
			log.debug("Channel path not found: {}", path);
			return FrontUtils.pageNotFound(request, response, model);
		}
		//检查是否单页
		if(checkAlone){
			if(channel.getHasContent()){
				return FrontUtils.pageNotFound(request, response, model);
			}
		}
		model.addAttribute("channel", channel);
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		String equipment=(String) request.getAttribute("ua");
		if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
			return channel.getMobileTplChannelOrDef();
		}
		return channel.getTplChannelOrDef();
	}

	public String content(Integer id, int pageNo, String[] params,
			PageInfo info, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		Content content = contentMng.findById(id);
		if (content == null) {
			log.debug("Content id not found: {}", id);
			return FrontUtils.pageNotFound(request, response, model);
		}
		Integer pageCount=content.getPageCount();
		if(pageNo>pageCount||pageNo<0){
			return FrontUtils.pageNotFound(request, response, model);
		}
		//非终审文章
		CmsConfig config=CmsUtils.getSite(request).getConfig();
		Boolean preview=config.getConfigAttr().getPreview();
		if(config.getViewOnlyChecked()&&!content.getStatus().equals(ContentCheck.CHECKED)){
			return FrontUtils.showMessage(request, model, CONTENT_STATUS_FORBIDDEN);
		}
		CmsUser user = CmsUtils.getUser(request);
		CmsSite site = content.getSite();
		Set<CmsGroup> groups = content.getViewGroupsExt();
		int len = groups.size();
		// 需要浏览权限
		if (len != 0) {
			// 没有登录
			if (user == null) {
				session.setAttribute(request, response, "loginSource", "needPerm");
				return FrontUtils.showLogin(request, model, site);
			}
			// 已经登录但没有权限
			Integer gid = user.getGroup().getId();
			boolean right = false;
			for (CmsGroup group : groups) {
				if (group.getId().equals(gid)) {
					right = true;
					break;
				}
			}
			//无权限且不支持预览
			if (!right&&!preview) {
				String gname = user.getGroup().getName();
				return FrontUtils.showMessage(request, model, GROUP_FORBIDDEN,
						gname);
			}
			//无权限支持预览
			if(!right&&preview){
				model.addAttribute("preview", preview);
				model.addAttribute("groups", groups);
			}
		}
		//收费模式
		if(content.getCharge()){
			if(user==null){
				session.setAttribute(request, response, "loginSource", "charge");
				return FrontUtils.showLogin(request, model, site);
			}else{
				//非作者且未购买
				if(!content.getUser().equals(user)){
					//用户已登录判断是否已经购买
					boolean hasBuy=contentBuyMng.hasBuyContent(user.getId(), content.getId());
					if(!hasBuy){
						try {
							String rediretUrl="/content/buy.jspx?contentId="+content.getId();
							if(StringUtils.isNotBlank(site.getContextPath())){
								rediretUrl=site.getContextPath()+rediretUrl;
							}
							response.sendRedirect(rediretUrl);
						} catch (IOException e) {
							//e.printStackTrace();
						}
					}
				}
			}
		}
		String txt = content.getTxtByNo(pageNo);
		// 内容加上关键字
		txt = cmsKeywordMng.attachKeyword(site.getId(), txt);
		Paginable pagination = new SimplePage(pageNo, 1, content.getPageCount());
		model.addAttribute("pagination", pagination);
		FrontUtils.frontPageData(request, model);
		model.addAttribute("content", content);
		model.addAttribute("channel", content.getChannel());
		model.addAttribute("title", content.getTitleByNo(pageNo));
		model.addAttribute("txt", txt);
		model.addAttribute("pic", content.getPictureByNo(pageNo));
		FrontUtils.frontData(request, model, site);
		String equipment=(String) request.getAttribute("ua");
		if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
			return content.getMobileTplContentOrDef(content.getModel());
		}
		return content.getTplContentOrDef(content.getModel());
	}
	

	private boolean existIndexPage(CmsSite site){
		boolean exist=false;
		if(site.getStaticIndex()){
			File indexPage;
			if(site.getIndexToRoot()){
				indexPage=new File(realPathResolver.get(INDEX_HTML));
			}else{
				indexPage=new File(realPathResolver.get(site.getStaticDir()+INDEX_HTML));
			}
			if(indexPage.exists()){
				exist=true; 
			}
		}
		return exist;
	}
	
	private String goToIndexPage(HttpServletRequest request,HttpServletResponse response,CmsSite site){
		String equipment=(String) request.getAttribute("ua");
		try {
			String ctx="";
			if(StringUtils.isNotBlank(site.getContextPath())){
				ctx=site.getContextPath();
			}
			if(site.getIndexToRoot()){
				if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
					response.sendRedirect(ctx+INDEX_HTML_MOBILE);
				}else{
					response.sendRedirect(ctx+INDEX_HTML);
				}
			}else{
				if(StringUtils.isNotBlank(equipment)&&equipment.equals("mobile")){
					response.sendRedirect(ctx+site.getStaticMobileDir()+INDEX_HTML);
				}else{
					response.sendRedirect(ctx+site.getStaticDir()+INDEX_HTML);
				}
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_INDEX, TPL_INDEX); 
	}
	

	@Autowired
	private ChannelMng channelMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private CmsKeywordMng cmsKeywordMng;
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private ContentBuyMng contentBuyMng;
	@Autowired
	private SessionProvider session;
}
