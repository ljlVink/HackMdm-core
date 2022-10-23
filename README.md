# hackmdm core V2

使用方法

1.在第一个UI界面中预加载,不建议再application加载有可能会出问题


NewUI.java

```java
new HackMdm(this).initMDM();
```

初次加载

```java
HackMdm.DeviceMDM.initHack(0);
```

第二次加载，例如onResume
```java
HackMdm.DeviceMDM.initHack(1);
```

