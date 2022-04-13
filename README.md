# PA_CA_Software
项目在PA_CA_Software/src/software文件夹下  
PA_CA_Software/src/attack是各部分功能的原始代码，可以辅助理解 
  
本系统命名为“PA&CA Software”，目前包括五个主要功能：  
(1)选择网卡  
  列举出本机所有可用网络接口，供用户选择，在虚拟机使用NAT网络模式的情况下，VMware Virtual Ethernet Adapter for VMnet8是默认的虚拟机网卡。选择完网卡后可以进入后续操作。  
(2)协议分析  
  本系统模仿Wireshark实现了网络层数据包捕捉以及协议分析，有“开始”“暂停”“清空”“刷新”“过滤器”几个基本功能，在面板会显示数据包No.、Time、Source、Destination、Protocol、Length、Info的信息。  
(3)扫描主机  
  扫描主机功能是根据系统提供的网关对当前局域网内的存活主机进行扫描，全程预计50秒，可将存活主机的IP地址和MAC地址打印至表格中。  
(4)ARP攻击  
  根据扫描到的存活主机信息，可以选择对任一主机进行ARP攻击，目前实现的攻击有“中间人欺骗攻击”和“伪装网关欺骗攻击”，且可以实现单向欺骗和双向欺骗。  
(5)DDoS攻击  
  输入想要攻击的主机IP地址，选择“开始攻击”，即可对目标主机发起DDoS攻击，可以进入虚拟机查看攻击效果。  
![图片1](https://user-images.githubusercontent.com/62821148/163165162-c6a53b9b-7097-4da1-bc23-bb6647f944f5.png)
![图片2](https://user-images.githubusercontent.com/62821148/163165231-536948c1-ae0f-43c5-8972-bda77ecc191a.png)
![图片3](https://user-images.githubusercontent.com/62821148/163165239-2489cc06-7cd8-4521-81f6-25a019394ad9.png)
![图片4](https://user-images.githubusercontent.com/62821148/163165253-4e0f09ca-7c57-4bdc-b067-ded2cca2e2ac.png)
![图片5](https://user-images.githubusercontent.com/62821148/163165258-27228c4c-988c-4d4b-be2c-dfcd307d3326.png)
![图片6](https://user-images.githubusercontent.com/62821148/163165269-9f8ee3e6-12ff-46ed-9dde-f049802de319.png)
![图片7](https://user-images.githubusercontent.com/62821148/163165275-7f7bbf1c-700a-44f5-908a-84e16637736d.png)
![图片8](https://user-images.githubusercontent.com/62821148/163165281-a2d777d3-d67d-4bba-8976-f0a89e1a7e20.png)
