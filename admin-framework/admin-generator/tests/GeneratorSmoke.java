import java.nio.file.*;
import java.io.*;
import java.util.*;
import javax.tools.*;
import dev.geo.admin.generator.model.*;
import dev.geo.admin.generator.util.*;
import org.apache.velocity.app.Velocity;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;

public class GeneratorSmoke {
 static GenTableColumn col(String name, String type, String html, boolean pk) {
  GenTableColumn c=new GenTableColumn(); c.setId((long)name.hashCode());c.setColumnName(name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase());
  c.setColumnComment(name);c.setJavaField(name);c.setJavaType(type);c.setColumnType("varchar(100)");c.setHtmlType(html);c.setIsPk(pk?"1":"0");c.setIsIncrement(pk?"1":"0");c.setIsRequired("0");c.setIsInsert("1");c.setIsEdit(pk?"0":"1");c.setIsList("1");c.setIsQuery(pk?"0":"1");c.setQueryType("EQ");c.setDictType("");return c;
 }
 static GenTable table(String name,String category) {
  GenTable t=new GenTable();t.setId(1L);t.setTableName("demo_"+name.toLowerCase());t.setClassName(name);t.setBusinessName(name.toLowerCase());t.setModuleName("system");t.setPackageName("dev.geo.admin.system");t.setFunctionName("测试\"业务");t.setFunctionAuthor("smoke");t.setTplCategory(category);t.setFormColNum(2);t.setOptions("{\"genView\":true}");
  List<GenTableColumn> cols=new ArrayList<>(List.of(col("id","Long","input",true), col("name","String","input",false),col("amount","BigDecimal","input",false),col("enabled","Boolean","radio",false),col("day","LocalDate","datetime",false),col("eventTime","Instant","datetime",false),col("localTime","LocalDateTime","datetime",false),col("image","String","image",false),col("file","String","upload",false),col("content","String","editor",false),col("status","Integer","select",false),col("tags","String","checkbox",false)));
  cols.get(1).setIsRequired("1");cols.get(1).setQueryType("LIKE");cols.get(2).setQueryType("BETWEEN");cols.get(4).setQueryType("BETWEEN");cols.get(10).setDictType("sys_normal_disable");cols.get(11).setDictType("sys_normal_disable");
  t.setColumns(cols);t.setPkColumn(cols.get(0));return t;
 }
 public static void main(String[] args) throws Exception {
  Path out=Path.of(args[0]);Files.createDirectories(out);VelocityInitializer.initVelocity();
  List<GenTable> tables=new ArrayList<>();tables.add(table("Sample","crud"));
  GenTable tree=table("Category","tree");tree.getColumns().add(col("parentId","Long","input",false));tree.setOptions("{\"genView\":true,\"treeCode\":\"id\",\"treeParentCode\":\"parent_id\",\"treeName\":\"name\"}");tables.add(tree);
  GenTable main=table("Orders","sub"),sub=table("OrderItem","crud");sub.getColumns().add(col("orderId","Long","input",false));main.setSubTable(sub);main.setSubTableName(sub.getTableName());main.setSubTableFkName("order_id");tables.add(main);
  GenTable manual=table("Manual","crud");manual.getPkColumn().setIsIncrement("0");manual.getPkColumn().setJavaType("String");tables.add(manual);
  GenTable audit=table("Audited","crud");
  for(String name:List.of("creatorId","createBy","createTime","updaterId","updateBy","updateTime","remark")) {
    GenTableColumn c=col(name,name.endsWith("Id")?"Long":name.endsWith("Time")?"Instant":"String","input",false);
    c.setIsQuery("0");if(c.isAuditColumn()){c.setIsInsert("0");c.setIsEdit("0");}audit.getColumns().add(c);
  }
  audit.getColumns().add(col("startTime","LocalTime","datetime",false)); tables.add(audit);
  List<String> javaFiles=new ArrayList<>();int count=0;
  for(GenTable t:tables) {
   var context=VelocityUtils.prepareContext(t);
   for(String template:VelocityUtils.getTemplateList(t)) {
    StringWriter writer=new StringWriter();Velocity.getTemplate(template,"UTF-8").merge(context,writer);
    String body=writer.toString();Path file=out.resolve(VelocityUtils.getFileName(template,t));Files.createDirectories(file.getParent());Files.writeString(file,body);
    if(body.contains("com.ruoyi") || body.contains("${column.") || body.contains("${ClassName}")) throw new AssertionError("Unresolved template: "+template);
    if(file.toString().endsWith(".java"))javaFiles.add(file.toString());count++;
   }
  }
  List<String> javac=new ArrayList<>(List.of("-encoding","UTF-8","-parameters","-classpath",System.getProperty("java.class.path"),"-d",out.resolve("classes").toString()));javac.addAll(javaFiles);
  int status=ToolProvider.getSystemJavaCompiler().run(null,System.out,System.err,javac.toArray(String[]::new));if(status!=0)throw new AssertionError("Generated Java failed: "+status);
  MybatisConfiguration cfg=new MybatisConfiguration();
  for(String xml:List.of("GenTableMapper.xml","GenTableColumnMapper.xml")) {
   String path="mapper/generator/"+xml;
   try(InputStream in=GeneratorSmoke.class.getClassLoader().getResourceAsStream(path)) { new XMLMapperBuilder(in,cfg,path,cfg.getSqlFragments()).parse(); }
  }
  try(var loader=new java.net.URLClassLoader(new java.net.URL[]{out.resolve("classes").toUri().toURL()}, GeneratorSmoke.class.getClassLoader())) {
   Class<?> serviceType=loader.loadClass("dev.geo.admin.system.service.system.IManualService");
   Object service=java.lang.reflect.Proxy.newProxyInstance(loader,new Class[]{serviceType},(proxy,method,params)->"manual-key");
   Class<?> dto=loader.loadClass("dev.geo.admin.system.model.system.dto.ManualSaveDTO");
   Class<?> controller=loader.loadClass("dev.geo.admin.system.controller.system.ManualController");
   Object bean=controller.getConstructors()[0].newInstance(service,null);
   Object response=controller.getMethod("create",dto).invoke(bean,dto.getConstructor().newInstance());
   Object data=response.getClass().getMethod("getData").invoke(response);
   if(!"manual-key".equals(data))throw new AssertionError("String PK must be returned in data, not message");
  }
  dev.geo.admin.generator.model.dto.GenTablePageReqDTO query=new dev.geo.admin.generator.model.dto.GenTablePageReqDTO();
  query.setOrderByColumn("updateTime");query.setIsAsc("ascending");
  String sql=cfg.getMappedStatement("dev.geo.admin.generator.mapper.GenTableMapper.selectGenTablePage").getBoundSql(Map.of("query",query)).getSql().replaceAll("\\s+"," ");
  if(!sql.contains("update_time asc"))throw new AssertionError(sql);
  for(String file:List.of("generator-init.sql","generator-migrate.sql","generator-menu.sql")) {
    com.alibaba.druid.sql.SQLUtils.parseStatements(Files.readString(Path.of("sql",file)),com.alibaba.druid.DbType.mysql);
  }
  dev.geo.admin.common.config.AppConfig config=new dev.geo.admin.common.config.AppConfig();config.setProfile(out.resolve("upload-check").toString());
  String upload=dev.geo.admin.common.config.AppConfig.getUploadPath();
  org.springframework.web.multipart.MultipartFile file=new org.springframework.web.multipart.MultipartFile() {
    public String getName(){return "file";} public String getOriginalFilename(){return "sample.png";}
    public String getContentType(){return "image/png";}public boolean isEmpty(){return false;}public long getSize(){return 4;}
    public byte[] getBytes(){return new byte[]{1,2,3,4};}public InputStream getInputStream(){return new ByteArrayInputStream(getBytes());}
    public void transferTo(File destination)throws IOException{Files.write(destination.toPath(),getBytes());}
  };
  String path=dev.geo.admin.common.utils.file.FileUploadUtils.upload(upload,file,dev.geo.admin.common.utils.file.MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION,true);
  if(!path.matches("/profile/upload/\\d{4}/\\d{2}/\\d{2}/[a-f0-9]+\\.png"))throw new AssertionError(path);
  try{dev.geo.admin.common.utils.file.FileUploadUtils.getAbsoluteFile(upload,"../../escape.png");throw new AssertionError("Traversal accepted");}catch(IOException expected){}
  System.out.println("PASS: SQL parsing, metadata sort binding, string-key response, local file upload and path validation");
  System.out.println("PASS: "+count+" generated files, "+javaFiles.size()+" Java files compiled; 2 MyBatis XML mappings parsed");
 }
}
