package callcount.gui_web;

import callcount.gui_web.callcount.gui_web.model.Project;
import callcount.gui_web.callcount.gui_web.model.Projects;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.util.FileCopyUtils;

import callcount.gui_web.model.UploadItem;
import callcount.lib.Main;

import javax.management.modelmbean.ModelMBean;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletResponse;

@Controller
public class GuiController {

    public static String PATH = "/tmp/vladimir/fly/";//"C:\\fly\\";
    public static String OUT_FILE = "out.txt";

    public static void unzip(File file, File root) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File f = new File(root, entry.getName());
            if (entry.isDirectory())
                continue;
            f.getParentFile().mkdirs();
            f.createNewFile();
            InputStream fin = zipFile.getInputStream(entry);
            OutputStream fout = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int read;
            while ((read = fin.read(buf)) != -1) {
                fout.write(buf, 0, read);
            }
            fin.close();
            fout.close();
        }
    }

    @RequestMapping(value = "/")
    public String main(Model model) {
        File root = new File(PATH);
        List<Project> projects = new ArrayList<Project>();
        for (String name: root.list()) {
            Project proj = new Project();
            proj.setName(name);
            projects.add(proj);
        }
        Projects projectsC = new Projects();
        projectsC.setProjects(projects);
        model.addAttribute("projects", projectsC);
        return "main";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String uploadForm(Model model) {
        model.addAttribute(new UploadItem());
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String getFile(UploadItem uploadItem, BindingResult result) {
        if (result.hasErrors()) {
            return "upload";
        }

        try {
            InputStream fin = uploadItem.getFileData().getInputStream();
            String dir = PATH + uploadItem.getName() + "/";
            String dirFs = dir + "u/";
            File dirF = new File(dirFs);
            if (!dirF.exists()) {
                dirF.mkdirs();
            }
            File zFile = new File(dir + "packed.zip");
            OutputStream fout = new FileOutputStream(zFile);
            byte[] buf = new byte[1024];
            int read;
            while ((read = fin.read(buf)) != -1) {
                fout.write(buf, 0, read);
            }
            fin.close();
            fout.close();
            unzip(zFile, dirF);
            zFile.delete();
            Main.analyze(dirF, dirFs + uploadItem.getJar(), uploadItem.getCls(),
                    uploadItem.getPkgs(), dir + "/" + OUT_FILE, uploadItem.getArgs());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/download", params = {"name"})
    public String download(HttpServletResponse response,
                           @RequestParam(value = "name") String name) {
        File file = new File(PATH + name + "/" + OUT_FILE);
        response.setContentType("text/plain");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        try {
            FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        } catch (Exception e) {
        }
        return null;
    }
}