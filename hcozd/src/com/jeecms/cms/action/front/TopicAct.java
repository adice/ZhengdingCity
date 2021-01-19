package com.jeecms.cms.action.front;

import static com.jeecms.cms.Constants.TPLDIR_TOPIC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.CmsTopic;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.CmsTopicMng;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;

@Controller
public class TopicAct {

	public static final String TOPIC_INDEX = "tpl.topicIndex";
	public static final String TOPIC_CHANNEL = "tpl.topicChannel";
	public static final String TOPIC_DEFAULT = "tpl.topicDefault";
	public static final String TOPIC_REVOLUTION = "tpl.topicRevolution";// 革命史
	public static final String TOPIC_REVOLUTION_INDEX = "tpl.topicRevolutionIndex";// 革命史
	public static final String TOPIC_EDUCATION = "tpl.topicEducation";// 教育
	public static final String TOPIC_CHOROGRAPHY = "tpl.topicChorography";// 地方志

	@RequestMapping(value = "/topic*.jspx", method = RequestMethod.GET)
	public String index(Integer channelId, Integer topicId, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		// 全部？按站点？按栏目？要不同模板？
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		if (topicId != null) {
			CmsTopic topic = cmsTopicMng.findById(topicId);
			if (topic == null) {
				return FrontUtils.pageNotFound(request, response, model);
			}
			model.addAttribute("topic", topic);
			String tpl = topic.getTplContent();
			if (StringUtils.isBlank(tpl)) {
				tpl = FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_TOPIC, TOPIC_DEFAULT);
			}
			return tpl;
		} else if (channelId != null) {
			Channel channel = channelMng.findById(channelId);
			if (channel != null) {
				model.addAttribute("channel", channel);
			} else {
				return FrontUtils.pageNotFound(request, response, model);
			}
			return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_TOPIC, TOPIC_CHANNEL);
		} else {
			return FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_TOPIC, TOPIC_INDEX);
		}
	}

	@RequestMapping(value = "/topic/{id}_*.jspx", method = RequestMethod.GET)
	public String topic(@PathVariable String id, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		if (id == null) {
			return FrontUtils.pageNotFound(request, response, model);
		}
		Integer topicId = null;
		try {
			topicId = Integer.parseInt(id);
		} catch (Exception e) {
			return FrontUtils.pageNotFound(request, response, model);
		}
		CmsTopic topic = null;
		if (topicId != null) {
			topic = cmsTopicMng.findById(topicId);
		}
		if (topic == null) {
			return FrontUtils.pageNotFound(request, response, model);
		}
		model.addAttribute("topic", topic);
		// 检索参数
		String searchParam = request.getParameter("searchParam") != null ? request.getParameter("searchParam") : "";
		model.addAttribute("searchParam", searchParam);
		String tpl = topic.getTplContent();
		if (StringUtils.isBlank(tpl)) {
			if (topic.getId() == 1) {
				String topicTitle = request.getParameter("topicTitle");
				if (topicTitle != null && !topicTitle.equals("")) {
					String channelId = request.getParameter("channelId") != null ? request.getParameter("channelId") : "";
					model.addAttribute("channelId", channelId);
					model.addAttribute("topicTitle", topicTitle);
					if ("报刊".equals(topicTitle) || "档案".equals(topicTitle)) {
						model.addAttribute("allParam", searchParam);
					} else {
						if (!"".equals(searchParam)) {
							model.addAttribute("allParam", topicTitle + "%" + searchParam);
						} else {
							model.addAttribute("allParam", topicTitle);
						}
					}
					tpl = FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_TOPIC, TOPIC_REVOLUTION);
				} else {
					tpl = FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_TOPIC, TOPIC_REVOLUTION_INDEX);
				}
			} else if (topic.getId() == 2) {
				tpl = FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_TOPIC, TOPIC_EDUCATION);
			} else if (topic.getId() == 3) {
				tpl = FrontUtils.getTplPath(request, site.getSolutionPath(), TPLDIR_TOPIC, TOPIC_CHOROGRAPHY);
			}
		}
		FrontUtils.frontData(request, model, site);
		FrontUtils.frontPageData(request, model);
		return tpl;
	}

	@Autowired
	private CmsTopicMng cmsTopicMng;
	@Autowired
	private ChannelMng channelMng;
}
