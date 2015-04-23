package lab.s2jh.module.ad.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.core.cons.GlobalConstant;
import lab.s2jh.core.entity.BaseNativeEntity;
import lab.s2jh.core.web.json.DateTimeJsonSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Access(AccessType.FIELD)
@Table(name = "m_proxy",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"ip", "port"})})
@MetaData(value = "代理")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProxyInfo extends BaseNativeEntity implements Comparable<ProxyInfo> {

    private static final long serialVersionUID = 9036755181626781283L;

    @MetaData(value = "IP地址")
    @Column(nullable = false, length = 16)
    private String ip;

    @MetaData(value = "端口")
    @Column(nullable = false)
    private int port;

    @MetaData(value = "代理类型")
    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private GlobalConstant.ProxyType proxyType;

    @MetaData(value = "抓取时间")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    @Column(name = "crawl_time", nullable = false)
    private Date crawlTime;

    @MetaData(value = "校验时间")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    @Column(name = "check_time", nullable = true)
    private Date checkTime;

    @MetaData(value = "校验状态")
    @Column(name = "check_flag", nullable = false)
    private Integer checkFlag = 0;

    @Override
    @Transient
    public String getDisplay() {
        return ip + ":" + port;
    }

    @Override
    public int compareTo(ProxyInfo o) {
        return CompareToBuilder.reflectionCompare(o.getIp() + ":" + o.getPort(), ip + ":" + port);
    }

    @Override
    public String toString() {
        return "ProxyInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", proxyType=" + proxyType +
                ", crawlTime=" + crawlTime +
                ", checkTime=" + checkTime +
                ", checkFlag=" + checkFlag +
                '}';
    }
}
