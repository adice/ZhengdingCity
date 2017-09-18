package com.baidu.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.util.Util;

/**
* @ClassName: UploadHandleServlet
* @Description: 
* @date: 2015-1-3 下午11:35:50
*
*/ 
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	response.setCharacterEncoding("UTF-8");
	    String savePath = this.getServletContext().getRealPath("/WEB-INF/yuyin");
	    File filePath=new File(savePath);
	    if(!filePath.exists()){
	    	filePath.mkdirs();
	    }
	    String message="";
	    try{
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        factory.setSizeThreshold(1024*100);//设置缓冲区的大小为100KB，如果不指定，那么缓冲区的大小默认是10KB
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        upload.setHeaderEncoding("UTF-8"); 
	        if(!ServletFileUpload.isMultipartContent(request)){
	            return;
	        }
	        upload.setFileSizeMax(1024*1024);
	        upload.setSizeMax(1024*1024*10);
	        List<FileItem> list = upload.parseRequest(request);
	        for(FileItem item : list){
	            if(item.isFormField()){
	                String name = item.getFieldName();
	                String value = item.getString("UTF-8");
	            }else{
	                String filename = item.getName();
	                if(filename==null || filename.trim().equals("")){
	                    continue;
	                }
	                filename = filename.substring(filename.lastIndexOf("\\")+1);
	                String fileExtName = filename.substring(filename.lastIndexOf(".")+1);
	                InputStream in = item.getInputStream();
	                String saveFilename = makeFileName(filename);
	                FileOutputStream out = new FileOutputStream(savePath+"\\"+saveFilename+".wav");
	                byte buffer[] = new byte[1024];
	                int len = 0;
	                while((len=in.read(buffer))>0){
	                    out.write(buffer, 0, len);
	                }
	                in.close();
	                out.close();
	                message = "load";
	                // 初始化一个FaceClient
	                AipSpeech client = new AipSpeech("10007588", "MZOIfH4eGid2EWgFLU3dEzK8", "55aa115b4347a755038fde7ad046f401");
	                // 可选：设置网络连接参数
	                client.setConnectionTimeoutInMillis(2000);
	                client.setSocketTimeoutInMillis(60000);
	                // 调用API
	                System.out.println(savePath+"\\"+saveFilename+".wav");
	                byte[] data = Util.readFileByBytes(savePath+"\\"+saveFilename+".wav");
//                  JSONObject res = client.asr(data, "wav", 16000, null);
	                JSONObject res = client.asr("D:\\audio.wav", "wav", 16000, null);
	                System.out.println(res.toString(2));
	                if(!res.get("err_msg").equals("success.")){
	                	message="解析失败";
	                }else
	                	message=res.get("result").toString().substring(2, res.get("result").toString().length()-2);
	            }
	        }
	    }catch (FileUploadBase.FileSizeLimitExceededException e) {
	        e.printStackTrace();
	        message="解析失败";
	    }catch (FileUploadBase.SizeLimitExceededException e) {
	        e.printStackTrace();
	        message="解析失败";
	    }catch (Exception e) {
	    	message="解析失败";
	        e.printStackTrace();
	    }
	    response.getWriter().write(message);
    }
    
    /**
    * @Method: makeFileName
    * @Description: 生成上传文件的文件名，文件名以：uuid+"_"+文件的原始名称
    * @param filename 文件的原始名称
    * @return uuid+"_"+文件的原始名称
    */ 
    private String makeFileName(String filename){
        return UUID.randomUUID().toString() + "_" + filename;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}