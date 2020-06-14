import java.lang.ref.PhantomReference;
import java.util.Objects;

public class Doc implements Comparable  {
    private String title;
    private String titleHtml;
    private String id;
    private Integer line;
    //文件还是文件夹 0 文件
    private Integer  fileType=0;
    //html内容
    private String html;
    //记录一次
    private Integer log;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitleHtml() {
        return titleHtml;
    }

    public void setTitleHtml(String titleHtml) {
        this.titleHtml = titleHtml;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getLog() {
        return log;
    }

    public void setLog(Integer log) {
        this.log = log;
    }


    @Override
    public int compareTo(Object o) {
        Doc doc = (Doc) o;
        return this.line.compareTo(doc.getLine());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doc doc = (Doc) o;
        return Objects.equals(id, doc.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
