# Mpush-client

## 基于websocket的即时消息推送服务(安卓客户端)

> 类似[server酱](http://sc.ftqq.com)的消息推送服务  
> 服务端见这个仓库[Mpush](https://github.com/kooritea/mpush)

---
第一次写安卓，代码写得非常辣鸡

只测试了MIUI10安卓7.0，其他版本我也不清楚会怎么样- -

# 一、安装
因为是即时消息推送服务，所以需要给`自启动`和`无限制的电池策略`以保证消息即时性

后台运行的服务仅有一个websocket连接，在我的手机上仅占用17mb内存

- 自启动主要用于服务被杀掉的时候重启和完全后台运行
- 打开自启动后即使是在任务管理器划掉也能继续保持后台运行
- 无限制的的电池策略用于关闭屏幕的时候保持心跳（暂时不支持调整心跳间隔，现在设定是一分钟一次，实际上会受系统熄屏关闭cpu后同步唤醒的限制）

# 二、设置
右上角的菜单有设置界面  
需要设置连接地址、TOKEN、DEVICE_ID  
这些配置只要按照server端的配置填写即可  

# 三、下载
[发布页](https://github.com/kooritea/mpush-android-client/releases)可以下载编译好的APK

# 四、预览
![1](https://ww2.sinaimg.cn/large/007eZ24Wgy1g3e1no5xd7j30dg0mbmxz)
![2](https://ww2.sinaimg.cn/large/007eZ24Wgy1g3e1o3cw7jj30df0lp3ym)
![3](https://ww2.sinaimg.cn/large/007eZ24Wgy1g3e1oeqk7rj30dk0lpwep)
![4](https://ww2.sinaimg.cn/large/007eZ24Wgy1g3e1ojz6iwj30dg0ltwfa)