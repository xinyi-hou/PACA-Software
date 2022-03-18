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
