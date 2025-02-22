# 公众号接入AI大模型使用手册

## 1. 注册/登录公众号AI助手

#### 1.1注册[公众号AI助手平台](https://www.wxopen-ai.cn/)

![image-20250219212436905](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250219212436905.png)

#### 1.2 创建一个账户，注册并登录，进入如下界面

![image-20250219212544430](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250219212544430.png)

## 2. 登录公众号获取信息

公众号名称填写自己的公众号名称，开发者ID、开发者密码、令牌、消息加解密密钥，去[**微信公众平台**](https://mp.weixin.qq.com/)获取

#### 2.1 找到 `设置与开发`

![image-20250215211611905](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215211611905.png)

#### 2.2 找到 `开发接口管理`

![image-20250215211657718](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215211657718.png)

#### 2.3 找到开发者ID(AppID) 和 开发者密码(AppSecret)，复制下来，保存好

![image-20250215211821363](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215211821363.png)

#### 2.4 找到 `令牌(Token)`和`消息加解密密钥`

- 点击修改配置 、如果是第一次配置的话，新建一个配置就好了

![image-20250215212038056](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215212038056.png)

-  进入配置页面，按照如下图要求，保存好我们的 `Token` 和  `消息加解密密钥` ，加密方式选择 `明文模式`即可

​	![image-20250215212641645](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215212641645.png)

## 3. 回到公众号AI助手进行配置

#### 3.1 把上面我们得到的`AppID`、`开发者密码(AppSecret)`、`令牌(Token)`、`消息加解密密钥`复制到页面中对应的地方，点击保存配置

![image-20250219212625464](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250219212625464.png)

#### 3.2 得到如下页面，复制`服务器地址URL` 到刚才的微信公众号页面中去

![image-20250219212830703](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250219212830703.png)

#### 3.3 提交信息

![image-20250215213629918](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215213629918.png)

#### 3.4 点击启用

![image-20250215213910714](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215213910714.png)

5. 成功反馈，会提示提交成功，到这里就完成了全部配置了

![image-20250215213705175](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215213705175.png)

## 4. 体验一下

![image-20250215214305974](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215214305974.png)

回答问题速度相当快，问答问题也不啰嗦，这个模型本人也是训练了很久才达到这种效果！

## 5. 其他功能

#### 5.1 关注公众号自动回复

写好文案保存设置即可

![image-20250219212848449](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250219212848449.png)

![image-20250215214712754](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215214712754.png)

#### 5.2 打造公众号资源列表

![image-20250219212908334](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250219212908334.png)

回复口令就可以领取资源哈！效果如下

![image-20250215214857875](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250215214857875.png)

## 6. 满血版本DeepSeek对话

本网站已经接入满血版DeepSeek供大家免费使用！

![image-20250222123941659](https://hynzdc.oss-cn-beijing.aliyuncs.com/image/image-20250222123941659.png)

## 6. 写在最后

市场上很多api都在套壳，弄虚作假，更受不了的是收费还很贵。市场上虽然有很多插件，但是响应速度还很慢，本作品在代码层面优化了算力资源分配，尽可能给大家提供流畅的公众号AI智能服务，项目之后会开源，关注公众号：进化码 获取更多资源🚀。