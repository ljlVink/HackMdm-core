# hackmdm core V3


使用方法

首次加载：请不要在application加载，要在activity中加载，渲染抽象类

```java
new HackMdm(this).initMDM();
```

进行第一次hack，注：本方法在程序内运行一次就可以，建议与initMDM放在一起。

```java
HackMdm.DeviceMDM.initHack(0);
```

二次加载，例如当返回到程序时候会重新应用一些策略。

```java
HackMdm.DeviceMDM.initHack(1);
```

---

请遵守gplv3开源协议。
