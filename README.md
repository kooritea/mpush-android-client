# Mpush 安卓客户端2.0

- 适配Mpush2.0(不兼容mpush1.0)
- 和1.0客户端几乎没有太大变化
- 稍微降低了一点内存占用

## Todo
- []markdown
- []通知优先级
- []推送信息
- [x]scheme

## 下载apk
[releases](https://github.com/kooritea/mpush-android-client/releases)

## 服务端仓库
[mpush](https://github.com/kooritea/mpush/tree/2.0)

## 注意事项
- 和1.0一样,授予自启动权限后才能够完全后台运行(在任务滚利器中划掉也可以继续运行)
- [1.x客户端](https://github.com/kooritea/mpush-android-client/tree/master)要配合[1.x服务端](https://github.com/kooritea/mpush/tree/master)使用
- [2.x客户端](https://github.com/kooritea/mpush-android-client/tree/2.x)要配合[2.x服务端](https://github.com/kooritea/mpush/tree/2.0)使用

## 功能
- 和1.0一样,设置服务器地址和token之后 可以接受服务端推送的消息,并弹出通知
- 2.0客户端可以接受一个额外的scheme参数,以跳转到相应的app或浏览器

### scheme
```bash
POST https://your mpush server
{
  text: "B站直播通知",
  desp: "夏色祭",
  scheme: "bilibili://live/13946381"
}
```
这样 就可以通过点击通知或者消息详细页右上角直接打开B站app