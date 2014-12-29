package org.lff.client.listener;

/**
 * User: LFF
 * Datetime: 2014/12/27 11:57
 */
public class QueryType {
    public static final int A = 0x01; //指定计算机 IP 地址。  
    public static final int NS = 0x02; //指定用于命名区域的 DNS 名称服务器。  
    public static final int MD = 0x03;//指定邮件接收站（此类型已经过时了，使用MX代替） 
    public static final int MF = 0x04;//指定邮件中转站（此类型已经过时了，使用MX代替） 
    public static final int CNAME = 0x05;//指定用于别名的规范名称。  
    public static final int SOA = 0x06;//指定用于 DNS 区域的“起始授权机构”。  
    public static final int MB = 0x07;//指定邮箱域名。  //
    public static final int MG = 0x08;//指定邮件组成员。//  
    public static final int MR = 0x09;//指定邮件重命名域名。//   
    public static final int NULL = 0x0A;//指定空的资源记录  
    public static final int WKS = 0x0B;//描述已知服务。  
    public static final int PTR = 0x0C;//如果查询是 IP 地址，则指定计算机名；否则指定指向其它信息的指针。 
    public static final int HINFO = 0x0D;//指定计算机 CPU 以及操作系统类型。  
    public static final int MINFO = 0x0E;//指定邮箱或邮件列表信息。  
    public static final int MX = 0x0F; //指定邮件交换器。
    public static final int TXT=0x10; //指定文本信息。  
    public static final int UINFO=0x64; //指定用户信息。  
    public static final int UID=0x65; //指定用户标识符。  
    public static final int GID=0x66; //指定组名的组标识符。  
    public static final int ANY=0xFF; //指定所有数据类型。
}
