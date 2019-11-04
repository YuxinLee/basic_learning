package mail_learning;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.File;
import java.util.Date;
import java.util.Properties;

/**
 * 3. 带图片资源以及附件的邮件
 */
public class SendMailDemo3 {

    // 初始化参数
    private static Properties prop;
    // 发件人
    private static InternetAddress sendMan = null;
    static {
        prop = new Properties();
        prop.put("mail.transport.protocol", "smtp");	// 指定协议
        prop.put("mail.smtp.host", "localhost");		// 主机   stmp.qq.com
        prop.put("mail.smtp.port", 25);					// 端口
        prop.put("mail.smtp.auth", "true");				// 用户密码认证
        prop.put("mail.debug", "true");					// 调试模式
        try {
            sendMan = new InternetAddress("zhangsan@itcast.com");
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSend() throws Exception {
        // 1. 创建邮件会话
        Session session = Session.getDefaultInstance(prop);
        // 2. 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 3. 设置参数：标题、发件人、收件人、发送时间、内容
        message.setSubject("带图片邮件");
        message.setSender(sendMan);
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("lisi@itcast.com"));
        message.setSentDate(new Date());

        /*
         * 带附件(图片)邮件开发
         */
        // 构建一个总的邮件块
        MimeMultipart mixed = new MimeMultipart("mixed");
        // ---> 总邮件快，设置到邮件对象中
        message.setContent(mixed);
        // 左侧： （文本+图片资源）
        MimeBodyPart left = new MimeBodyPart();
        // 右侧： 附件
        MimeBodyPart right = new MimeBodyPart();
        // 设置到总邮件块
        mixed.addBodyPart(left);
        mixed.addBodyPart(right);

        /******附件********/
        String attr_path = this.getClass().getResource("a.docx").getPath();
        DataSource attr_ds = new FileDataSource(new File(attr_path));
        DataHandler attr_handler = new DataHandler(attr_ds);
        right.setDataHandler(attr_handler);
        right.setFileName("a.docx");


        /***************设置邮件内容: 多功能用户邮件 (related)*******************/
        // 4.1 构建一个多功能邮件块
        MimeMultipart related = new MimeMultipart("related");
        // ----> 设置到总邮件快的左侧中
        left.setContent(related);

        // 4.2 构建多功能邮件块内容 = 左侧文本 + 右侧图片资源
        MimeBodyPart content = new MimeBodyPart();
        MimeBodyPart resource = new MimeBodyPart();

        // 设置具体内容: a.资源(图片)
        String filePath = App_3ImgAndAtta.class.getResource("8.jpg").getPath();
        DataSource ds = new FileDataSource(new File(filePath));
        DataHandler handler = new DataHandler(ds);
        resource.setDataHandler(handler);
        resource.setContentID("8.jpg");   // 设置资源名称，给外键引用

        // 设置具体内容: b.文本
        content.setContent("<img src='cid:8.jpg'/>  好哈哈！", "text/html;charset=UTF-8");

        related.addBodyPart(content);
        related.addBodyPart(resource);



        // 5. 发送
        Transport trans = session.getTransport();
        trans.connect("zhangsan", "888");
        trans.sendMessage(message, message.getAllRecipients());
        trans.close();
    }

}
