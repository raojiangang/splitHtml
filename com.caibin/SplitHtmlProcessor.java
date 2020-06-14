import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SplitHtmlProcessor {
    //yaihtml 文件的目录
    private static final String RESOUCE_PATH = "F:\\22";
    //头部样式路径
    private static final String HEADHTML = "F:\\33\\head.html";
    //生成文件存放位置
    private static final String TARGET_PATH = "E:\\chm";


    public static void main(String[] args) throws IOException {


        List<File> fileList = new ArrayList<File>();

        getAllPath(RESOUCE_PATH, fileList);
        if (fileList.isEmpty()) return;

        fileList.stream().forEach(file -> {
            String name = file.getName();

            String targetPath = TARGET_PATH + "\\" + name.replace(".html", "");
            System.out.println(file.getPath());
            try {
                splitHtml(file.getPath(), targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }


    private static void getAllPath(String fileDir, List<File> fileList) {

        File file = new File(fileDir);
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        // 遍历，目录下的所有文件
        for (File f : files) {
            if (f.isFile()) {
                fileList.add(f);
            } else if (f.isDirectory()) {
                System.out.println(f.getAbsolutePath());
                getAllPath(f.getAbsolutePath(), fileList);
            }
        }
    }

    private static void splitHtml(String resource, String targetPath) throws IOException {
        File file = new File(resource);
        List<Doc> docs = getDocsPoint(file);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        docs = docs.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<String, Doc> docMap = docs.stream().collect(Collectors.toMap(Doc::getId, s -> s));
        String content;
        int i = 0;
        Boolean flag = false;
        Integer endPoint = 0;
        while ((content = reader.readLine()) != null) {
            if (content != null && content.contains("class=\"curproject-name\"")) {
                flag = true;

            }
            if (flag) {
                //获取数据
                String exists = checkExists(content, docs, docMap);
                if (exists != null) {


                    Doc doc = docMap.get(exists);
                    doc.setTitleHtml(content);
                    doc.setLine(i);
                    if (content.contains("<h1")) {
                        doc.setFileType(1);
                    }
                }
            }

            if (content != null && content.contains("class=\"m-footer\"")) {
                endPoint = i;
            }
            i = i + 1;
        }


        System.out.println(docMap);
        docMap.values().stream().sorted().forEach(s -> System.out.println("line" + s.getLine()));
        System.out.println(endPoint);
        List<Doc> collect = docMap.values().stream().sorted().collect(Collectors.toList());


        readAndWriteHtml(targetPath, resource, collect, endPoint);
    }

    private static List<Doc> getDocsPoint(File file) throws IOException {
        Document html = Jsoup.parse(file, "utf-8");
        Elements headContent = html.select("div[class=table-of-contents]");

        Elements as = headContent.select("a");
        //获取ids
        return as.stream().map(a -> {
            String href = a.attr("href");
            String text = a.text();
            if (href != null && href != "") {
                Doc doc = new Doc();
                doc.setTitle(text);
                doc.setId(href.substring(1));
                doc.setLog(0);
                return doc;
            }
            return null;
        }).collect(Collectors.toList());
    }


    private static String checkExists(String content, List<Doc> docs, Map<String, Doc> map) {
        for (Doc doc : docs) {
            if (doc.getId() != null && doc.getId() != "" && content != null && content.contains("id=\"" + doc.getId() + "\"")) {
                Doc doc2 = map.get(doc.getId());
                if (doc2.getLog() == null || doc2.getLog() == 0) {
                    doc2.setLog(1);
                    map.put(doc.getId(), doc2);
                    String id = doc.getId();
                    return id;
                }
            }
        }
        return null;
    }


    private static void readAndWriteHtml(String targetPath, String resourcePath, List<Doc> docs, Integer endPoint) throws IOException {
        File file = new File(resourcePath);
        String path = targetPath;
        String filePath = "";

        for (int i = 0; i < docs.size(); i++) {
            Doc doc = docs.get(i);
            Integer start = doc.getLine();
            Integer end;
            if (i < docs.size() - 1) {
                end = docs.get(i + 1).getLine();
            } else {
                end = endPoint;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            if (doc.getFileType() == 1) {
                //创建上级目录
                filePath = path + "\\" + doc.getTitle();
                File saveFile = new File(filePath);
                if (!saveFile.exists()) {
                    saveFile.mkdirs();
                }
            } else {
                //读取头部样式
                File head = new File(HEADHTML);
                BufferedReader headBuffer = new BufferedReader(new FileReader(head));
                File filezz = new File(filePath);
                if (!filezz.exists()) {
                    filezz.mkdirs();
                }
                String filePathzz = filePath + "\\" + doc.getTitle().replace("/", "") + ".html";

                File saveFile = new File(filePathzz);
                if (!saveFile.exists()) {
                    System.out.println(filePathzz);
                    try {
                        saveFile.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
                //写入头部文件
                String headStr = headBuffer.lines().collect(Collectors.joining());
                writer.write(headStr);
                reader.lines().skip(start).limit(end - start).forEach(s -> {

                    try {
                        System.out.println(s);
                        writer.newLine();
                        writer.write(s);

                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.write("</head></body>");
                writer.close();
                System.out.println("===============================================");
            }
        }
    }


}
