package com.pinpin.model;

public class RegisterModel {
   /****个人信息****/
   public String trade ;//行业id
   public String career ;//职位value
   public String years ;//工作年限value
   public String location ;//期望工作地id
   public String tag ;//标签 [id,id,id]
   public String details ;//描述value
   /****账号信息****/
   public String gender ;//性别 value  0-男  1-女  2-不限
   public String purpose ;//工作 value  0-找工作  1-招牛人  2-求合伙  3-兼职
   public String username ;//姓名 value 
   public String birthdate ;//出生日期 value
   public String password ; 
   public String password_confirm ; 
   /****设置****/
   public String showGender ;//性别 value  0-男  1-女  2-不限
   public String lookFor ;//工作 value  0-找工作  1-招牛人  2-求合伙  3-兼职
   public String ageMin ; 
   public String ageMax ; 
   public String maskContact ;    // true|false
   public String maskCo ;         // true|false
   public String maskCoIds ;      // [id,id,id]
   
   
   
}
