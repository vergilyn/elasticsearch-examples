# canal

- 个人笔记：<https://docs.qq.com/doc/DWFdta0xNUGFZcXlY>
- <https://github.com/alibaba/canal>
- canal-server: <https://hub.docker.com/r/canal/canal-server>
- canal-admin: <https://hub.docker.com/r/canal/canal-admin>

1. canal-admin  
<http://127.0.0.1:8089>  admin/123456 (`canal_manager.sql`初始化)

2. **特别**  
因为 canal image 是基于`centos6.10`，当用win10 docker WSL2运行时会出现`exited(139)`。建议升级到centos7，或者：
```TEXT
# c:/user/{username} 目录下 新建或编辑 `.wslconfig`
[wsl2]
kernelCommandLine=vsyscall=emulate
```

see:  
- [Immediate bash segfault ExitCode 139 with centos:6.10 containers #7284](https://github.com/docker/for-win/issues/7284)
- <https://forums.docker.com/t/docker-run-exits-immediately-despite-detach-interactive-tty-and-providing-a-command/99444/4>



## canal-osbase
- <https://hub.docker.com/r/canal/osbase>

```dockerfile
FROM centos:centos6.10

MAINTAINER agapple (jianghang115@gmail.com)

env DOWNLOAD_LINK="http://download.oracle.com/otn-pub/java/jdk/8u181-b13/96a7b8442fe848ef90c96a2fad6ed6d1/jdk-8u181-linux-x64.rpm"
# install system
RUN \
    /bin/cp -f /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'root:Hello1234' | chpasswd && \
    groupadd -r admin && useradd -g admin admin && \
    yum install -y man && \
    yum install -y dstat && \
    yum install -y unzip && \
    yum install -y nc && \
    yum install -y openssh-server && \
    yum install -y tar && \
    yum install -y which && \
    yum install -y wget && \
    yum install -y perl && \
    yum install -y file && \
    ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_dsa_key && \
    ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key && \
    sed -ri 's/session    required     pam_loginuid.so/#session    required     pam_loginuid.so/g' /etc/pam.d/sshd && \
    sed -i -e 's/^#Port 22$/Port 2222/' /etc/ssh/sshd_config && \
    mkdir -p /root/.ssh && chown root.root /root && chmod 700 /root/.ssh && \
    yum install -y cronie && \
    sed -i '/session required pam_loginuid.so/d' /etc/pam.d/crond && \
    true

RUN \
    touch /var/lib/rpm/* && \ 
    wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=xxx; oraclelicense=accept-securebackup-cookie" "$DOWNLOAD_LINK" -O /tmp/jdk-8-linux-x64.rpm && \
    yum -y install /tmp/jdk-8-linux-x64.rpm && \
    /bin/rm -f /tmp/jdk-8-linux-x64.rpm && \

    echo "export JAVA_HOME=/usr/java/latest" >> /etc/profile && \
    echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> /etc/profile && \
    yum clean all && \
    true

CMD ["/bin/bash"]
```

## canal-server
- canal-server: <https://hub.docker.com/r/canal/canal-server>
```dockerfile
FROM canal/osbase:v2

MAINTAINER agapple (jianghang115@gmail.com)

# install canal
COPY image/ /tmp/docker/
COPY canal.deployer-*.tar.gz /home/admin/

RUN \
    cp -R /tmp/docker/alidata /alidata && \
    chmod +x /alidata/bin/* && \
    mkdir -p /home/admin && \
    cp -R /tmp/docker/admin/* /home/admin/  && \
    /bin/cp -f alidata/bin/lark-wait /usr/bin/lark-wait && \

    mkdir -p /home/admin/canal-server && \
    tar -xzvf /home/admin/canal.deployer-*.tar.gz -C /home/admin/canal-server && \
    /bin/rm -f /home/admin/canal.deployer-*.tar.gz && \

    mkdir -p home/admin/canal-server/logs  && \
    chmod +x /home/admin/*.sh  && \
    chmod +x /home/admin/bin/*.sh  && \
    chown admin: -R /home/admin && \
    yum clean all && \
    true

# 2222 sys , 8000 debug , 11111 canal , 11112 metrics
EXPOSE 2222 11111 8000 11112

WORKDIR /home/admin

ENTRYPOINT [ "/alidata/bin/main.sh" ]
CMD [ "/home/admin/app.sh" ]
```

## canal-admin
- canal-admin: <https://hub.docker.com/r/canal/canal-admin>
```dockerfile
FROM canal/osadmin:v2

MAINTAINER agapple (jianghang115@gmail.com)

# install canal
COPY image/ /tmp/docker/
COPY canal.admin-*.tar.gz /home/admin/

RUN \
    cp -R /tmp/docker/alidata /alidata && \
    chmod +x /alidata/bin/* && \
    mkdir -p /home/admin && \
    mkdir -p /home/admin/bin/ && \
    cp -R /tmp/docker/app_admin.sh /home/admin/app.sh  && \
    cp -R /tmp/docker/admin/* /home/admin/  && \
    /bin/cp -f alidata/bin/lark-wait /usr/bin/lark-wait && \

    mkdir -p /home/admin/canal-admin && \
    tar -xzvf /home/admin/canal.admin-*.tar.gz -C /home/admin/canal-admin && \
    /bin/rm -f /home/admin/canal.admin-*.tar.gz && \

    mkdir -p home/admin/canal-admin/logs  && \
    chmod +x /home/admin/*.sh  && \
    chmod +x /home/admin/bin/*.sh  && \
    chown admin: -R /home/admin && \
    yum clean all && \
    true

# 8089 web
EXPOSE 8089

WORKDIR /home/admin

ENTRYPOINT [ "/alidata/bin/main.sh" ]
CMD [ "/home/admin/app.sh" ]
```