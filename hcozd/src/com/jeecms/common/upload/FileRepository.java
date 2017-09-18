package com.jeecms.common.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * 本地文件存储
 */
public class FileRepository implements ServletContextAware {
	private Logger log = LoggerFactory.getLogger(FileRepository.class);

	public String storeByExt(String path, String ext, MultipartFile file)
			throws IOException {
		//String filename = UploadUtils.generateFilename(path, ext);
		//File dest = new File(getRealPath(filename));
		String fileName=UploadUtils.generateRamdonFilename(ext);
		String fileUrl =path+fileName;
		File dest = new File(getRealPath(path),fileName);
		dest = UploadUtils.getUniqueFile(dest);
		store(file, dest);
		String filePath=getRealPath(fileUrl);
		if(filePath==null){
			filePath=getRealPath("/");
		}
		String unzipPath=filePath.substring(0, filePath.lastIndexOf("."));
		try {
			if(filePath.endsWith("zip"))
				this.unzip(filePath, unzipPath);
			else if(filePath.endsWith("rar"))
				this.unrar(filePath, unzipPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	public String storeByFilename(String filename, MultipartFile file)
			throws IOException {
		if(filename!=null&&(filename.contains("/")||filename.contains("\\")||filename.indexOf("\0")!=-1)){
			return "";
		}
		File dest = new File(getRealPath(filename));
		store(file, dest);
		return filename;
	}
	
	public String storeByFilePath(String path,String filename, MultipartFile file)
			throws IOException {
		if(filename!=null&&(filename.contains("/")||filename.contains("\\")||filename.indexOf("\0")!=-1)){
			return "";
		}
		File dest = new File(getRealPath(path+filename));
		store(file, dest);
		return path+filename;
	}

	public String storeByExt(String path, String ext, File file)
			throws IOException {
		//String filename = UploadUtils.generateFilename(path, ext);
		//File dest = new File(getRealPath(filename));
		String fileName=UploadUtils.generateRamdonFilename(ext);
		String fileUrl =path+fileName;
		File dest = new File(getRealPath(path),fileName);
		dest = UploadUtils.getUniqueFile(dest);
		store(file, dest);
		return fileUrl;
	}

	public String storeByFilename(String filename, File file)
			throws IOException {
		File dest = new File(getRealPath(filename));
		store(file, dest);
		return filename;
	}

	private void store(MultipartFile file, File dest) throws IOException {
		try {
			UploadUtils.checkDirAndCreate(dest.getParentFile());
			file.transferTo(dest);
		} catch (IOException e) {
			log.error("Transfer file error when upload file", e);
			throw e;
		}
	}

	private void store(File file, File dest) throws IOException {
		try {
			UploadUtils.checkDirAndCreate(dest.getParentFile());
			FileUtils.copyFile(file, dest);
		} catch (IOException e) {
			log.error("Transfer file error when upload file", e);
			throw e;
		}
	}

	public File retrieve(String name) {
		return new File(ctx.getRealPath(name));
	}
	
	private String getRealPath(String name){
		String realpath=ctx.getRealPath(name);
		if(realpath==null){
			realpath=ctx.getRealPath("/")+name;
		}
		return realpath;
	}

	private ServletContext ctx;

	public void setServletContext(ServletContext servletContext) {
		this.ctx = servletContext;
	}
	/** 
     * 解压zip格式的压缩文件到指定位置 
     * @param zipFileName 压缩文件 
     * @param extPlace 解压目录 
     * @throws Exception 
     */  
    @SuppressWarnings("unchecked")
    public synchronized void unzip(String zipFileName, String extPlace) throws Exception {
        try {
            (new File(extPlace)).mkdirs();
            File f = new File(zipFileName);
            ZipFile zipFile = new ZipFile(zipFileName);
            if((!f.exists()) && (f.length() <= 0)) {
                throw new Exception("要解压的文件不存在!");
            }
            String strPath, gbkPath, strtemp;
            File tempFile = new File(extPlace);
            strPath = tempFile.getAbsolutePath();
            java.util.Enumeration e = zipFile.getEntries();
            while(e.hasMoreElements()){
                org.apache.tools.zip.ZipEntry zipEnt = (ZipEntry) e.nextElement();
                gbkPath=zipEnt.getName();
                if(zipEnt.isDirectory()){
                    strtemp = strPath + File.separator + gbkPath;
                    File dir = new File(strtemp);
                    dir.mkdirs();
                    continue;
                } else {
                    //读写文件
                    InputStream is = zipFile.getInputStream(zipEnt);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    gbkPath=zipEnt.getName();
                    strtemp = strPath + File.separator + gbkPath;
                    
                    //建目录
                    String strsubdir = gbkPath;
                    for(int i = 0; i < strsubdir.length(); i++) {
                        if(strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
                            String temp = strPath + File.separator + strsubdir.substring(0, i);
                            File subdir = new File(temp);
                            if(!subdir.exists())
                            subdir.mkdir();
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(strtemp);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int c;
                    while((c = bis.read()) != -1) {
                        bos.write((byte) c);
                    }
                    bos.close();
                    fos.close();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**   
     * 解压rar格式压缩包。   
     * 对应的是java-unrar-0.3.jar，但是java-unrar-0.3.jar又会用到commons-logging-1.1.1.jar   
     */    
    private void unrar(String sourceRar,String destDir) throws Exception{     
        Archive a = null;
        FileOutputStream fos = null;
        try{
            a = new Archive(new File(sourceRar));
            FileHeader fh = a.nextFileHeader();
            while(fh!=null){
                if(!fh.isDirectory()){
                    //1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                    String compressFileName = fh.getFileNameString().trim();
                    String destFileName = "";
                    String destDirName = "";
                    String temp=compressFileName.replaceAll("\\\\", "/");
                    temp=temp.substring(temp.lastIndexOf("/")+1, temp.length());
                    //非windows系统
                    if(File.separator.equals("/")){
                        destFileName = destDir + "\\" + temp;
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
                    //windows系统 
                    }else{
                        destFileName = destDir + "\\" + temp;
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
                    }
                    //2创建文件夹
                    File dir = new File(destDirName);
                    if(!dir.exists()||!dir.isDirectory()){
                        dir.mkdirs();
                    }
                    //3解压缩文件
                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
        }catch(Exception e){
            throw e;
        }finally{
            if(fos!=null){
                try{fos.close();fos=null;}catch(Exception e){e.printStackTrace();}
            }
            if(a!=null){
                try{a.close();a=null;}catch(Exception e){e.printStackTrace();}
            }
        }
    }
}
