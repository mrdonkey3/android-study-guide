package com.dk.android_art.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

/**
 * @create on 2020/6/20 15:57
 * @description 系统工具类
 * @author mrdonkey
 */
class SystemUtils private constructor() {
    companion object {
        @JvmStatic
        fun getProcessName(context: Context): String? {
            val myPid = android.os.Process.myPid()
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcesses = activityManager.runningAppProcesses
            return runningAppProcesses?.find { it.pid == myPid }?.processName
        }

        /**
         * 检验某个包名的应用是否安装
         */
        fun checkInstall(context: Context, packageName: String): Boolean {
            val installedPackages = context.packageManager.getInstalledPackages(0)
            return installedPackages.any { it.packageName.equals(packageName, true) }
        }

        /**
         *


         * mes = "alipayqr://platformapi/startapp?saId=10000003";手机充值
         * mes = "alipayqr://platformapi/startapp?saId=10000007";扫一扫
         * mes = "alipayqr://platformapi/startapp?saId=10000009";爱心捐赠
         * mes = "alipayqr://platformapi/startapp?saId=100000011";彩票首页
         * mes = "alipayqr://platformapi/startapp?saId=100000033";话费卡转让
         * mes = "alipayqr://platformapi/startapp?saId=10000110";关于
         * mes = "alipayqr://platformapi/startapp?saId=10000112";服务授权
         * mes = "alipayqr://platformapi/startapp?saId=20000116";转账
         *
         * mes = "alipayqr://platformapi/startapp?saId=20000056";付款码-----210
         *
         * mes =  alipays://platformapi/startapp?appId=20000003;账单
         * mes =  alipays://platformapi/startapp?appId=20000076;账单
         * mes = "alipays://platformapi/startapp?appId=20000006";切换账户
         * mes = "alipays://platformapi/startapp?appId=20000008";支付宝登出
         * mes = "alipays://platformapi/startapp?appId=20000009";手机号注册
         * mes = "alipays://platformapi/startapp?appId=20000010";绑定手机
         * mes = "alipays://platformapi/startapp?appId=20000011";客服帮助
         * mes = "alipays://platformapi/startapp?appId=20000013";修改支付密码
         * mes = "alipays://platformapi/startapp?appId=20000014";我的银行卡
         * mes = "alipays://platformapi/startapp?appId=20000015";找回登录密码
         * mes = "alipays://platformapi/startapp?appId=20000017";修改登录密码
         * mes = "alipays://platformapi/startapp?appId=20000019";余额
         * mes = "alipays://platformapi/startapp?appId=20000020";卡包
         * mes = "alipays://platformapi/startapp?appId=20000024";支付宝设置
         * mes = "alipays://platformapi/startapp?appId=20000027";账号切换
         * mes = "alipays://platformapi/startapp?appId=20000031";设置个人头像
         * mes = "alipays://platformapi/startapp?appId=20000032";余额宝
         * mes = "alipays://platformapi/startapp?appId=20000033";提现
         * mes = "alipays://platformapi/startapp?appId=20000038";身份验证
         * mes = "alipays://platformapi/startapp?appId=20000048";添加生活好
         * mes = "alipays://platformapi/startapp?appId=20000049";意见反馈
         * mes = "alipays://platformapi/startapp?appId=20000050";打开地图
         * mes = "alipays://platformapi/startapp?appId=20000057";账号管理
         * mes = "alipays://platformapi/startapp?appId=20000068";快速挂失
         * mes = "alipays://platformapi/startapp?appId=20000068";安全中心
         * mes = "alipays://platformapi/startapp?appId=20000071";城市一卡通
         * mes = "alipays://platformapi/startapp?appId=20000078";上银汇款
         * mes = "alipays://platformapi/startapp?appId=20000081";更多
         *
         *
         * mes = "alipays://platformapi/startapp?appId=20000122";首页活动
         * mes = "alipays://platformapi/startapp?appId=20000123";收钱
         *
         * mes = "alipayqr://platformapi/startapp?appId=68687017";年度账单
         * mes = "alipayqr://platformapi/startapp?appId=20000101";生活号
         * mes = "alipayqr://platformapi/startapp?appId=20000102";打开nfc
         * mes = "alipayqr://platformapi/startapp?appId=20000107";出境
         * mes = "alipayqr://platformapi/startapp?appId=20000108";挂号就诊
         * mes = "alipayqr://platformapi/startapp?appId=20000110";我的保障
         * mes = "alipayqr://platformapi/startapp?appId=20000115";设备管理
         * mes = "alipayqr://platformapi/startapp?appId=20000119";阿里游戏
         * mes = "alipayqr://platformapi/startapp?appId=20000118";芝麻信用
         * mes = "alipayqr://platformapi/startapp?appId=20000120";饿了么
         * mes = "alipayqr://platformapi/startapp?appId=20000123";收钱
         * mes = "alipayqr://platformapi/startapp?appId=20000125";首页
         * mes = "alipayqr://platformapi/startapp?appId=20000126";免费wifi
         * mes = "alipayqr://platformapi/startapp?appId=20000130";滴滴
         * mes = "alipayqr://platformapi/startapp?appId=20000132";亲情号
         * mes = "alipayqr://platformapi/startapp?appId=20000134";股票自选
         * mes = "alipayqr://platformapi/startapp?appId=20000135";火车票
         * mes = "alipayqr://platformapi/startapp?appId=20000136";游戏充值
         * mes = "alipayqr://platformapi/startapp?appId=20000139";酒店搜索
         * mes = "alipayqr://platformapi/startapp?appId=20000141";修改昵称
         * mes = "alipayqr://platformapi/startapp?appId=20000142";娱乐宝
         * mes = "alipayqr://platformapi/startapp?appId=20000143";火车票汽车票预定
         * mes = "alipayqr://platformapi/startapp?appId=20000146";我的淘宝
         * mes = "alipayqr://platformapi/startapp?appId=20000150";汇率换算
         * mes = "alipayqr://platformapi/startapp?appId=20000153";游戏中心
         * mes = "alipayqr://platformapi/startapp?appId=20000155";飞猪
         * mes = "alipayqr://platformapi/startapp?appId=20000157";国际机票查询
         * mes = "alipayqr://platformapi/startapp?appId=20000160";蚂蚁会员
         * mes = "alipayqr://platformapi/startapp?appId=20000161";理财小工具
         * mes = "alipayqr://platformapi/startapp?appId=20000162";羊城通
         * mes = "alipayqr://platformapi/startapp?appId=20000165";定期理财
         * mes = "alipayqr://platformapi/startapp?appId=20000161";指纹手势解锁
         * mes = "alipayqr://platformapi/startapp?appId=20000168";年度账单
         * mes = "alipayqr://platformapi/startapp?appId=20000176";红包
         * mes = "alipayqr://platformapi/startapp?appId=20000183";设置手势密码
         * mes = "alipayqr://platformapi/startapp?appId=20000161";指纹手势解锁设定界面
         * mes = "alipayqr://platformapi/startapp?appId=20000186";通讯录
         * mes = "alipayqr://platformapi/startapp?appId=20000161";绑定智能手环
         * mes = "alipayqr://platformapi/startapp?appId=20000197";首页-热门游戏
         * mes = "alipayqr://platformapi/startapp?appId=20000199";花呗
         * mes = "alipayqr://platformapi/startapp?appId=20000205";亲情圈
         * mes = "alipayqr://platformapi/startapp?appId=20000218";黄金
         * mes = "alipayqr://platformapi/startapp?appId=20000225";借条
         * mes = "alipayqr://platformapi/startapp?appId=20000227";卡包
         * mes = "alipayqr://platformapi/startapp?appId=20000234";刷脸
         * mes = "alipayqr://platformapi/startapp?appId=20000235";服务提醒
         * mes = "alipayqr://platformapi/startapp?appId=20000241";车险服务
         * mes = "alipayqr://platformapi/startapp?appId=20000243";总资产
         * mes = "alipayqr://platformapi/startapp?appId=20000248";个性签名
         * mes = "alipayqr://platformapi/startapp?appId=20000252";朋友模块
         * mes = "alipayqr://platformapi/startapp?appId=20000255";账户充值
         * mes = "alipayqr://platformapi/startapp?appId=20000266";邮箱账单
         * mes = "alipayqr://platformapi/startapp?appId=20000288";聊天室
         * mes = "alipayqr://platformapi/startapp?appId=20000290";可能认识的人
         * mes = "alipayqr://platformapi/startapp?appId=20000298";证书管理
         * mes = "alipayqr://platformapi/startapp?appId=20000301";多设备管理
         * mes = "alipayqr://platformapi/startapp?appId=20000305";支付宝内付款码声波付
         * mes = "alipayqr://platformapi/startapp?appId=20000307";暗号 ---400
         */
        fun startAliPay(context: Context) {
            if (!checkInstall(context, "com.eg.android.AlipayGphone")) {
                Toast.makeText(context,"未安装支付宝！",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent().apply {
                    data = Uri.parse("alipayqr://platformapi/startapp?saId=20000056")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
//                val intent = Intent()
//                intent.action = "com.dk.android_art.action1"
//                intent.addCategory("com.dk.android_art.category")
//                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
//                intent.setDataAndType(Uri.parse("content://xxx"), "text/plain")
                context.startActivity(intent)
            }
        }
    }
}