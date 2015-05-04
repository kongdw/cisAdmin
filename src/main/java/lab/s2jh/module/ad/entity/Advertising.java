package lab.s2jh.module.ad.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.core.entity.BaseNativeEntity;
import lab.s2jh.core.util.ExtStringUtils;
import lab.s2jh.core.util.WebFormatter;
import lab.s2jh.core.web.json.DateTimeJsonSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Access(AccessType.FIELD)
@Entity
@Table(name = "m_Advertising")
@MetaData(value = "广告")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Advertising extends BaseNativeEntity implements Comparable<Advertising>{

    private static final long serialVersionUID = 9036755181626781283L;
    @MetaData(value = "广告ID")
    @Column(nullable = false, length = 32)
    private String adId;

    @MetaData(value = "广告标题")
    @Column(nullable = false,length = 500)
    private String title;

    @MetaData(value = "广告URL")
    @Column(nullable = false)
    @Lob
    private String adUrl;

    @MetaData(value = "吧名")
    @Column(nullable = false)
    private  String baName;

    @MetaData(value = "目标次数")
    @Column(nullable = false)
    private Integer checkNum;


    @MetaData(value = "验证字符串",tooltips = "广告验证字符串信息")
    @Column(nullable = false)
    private String checkedStr;

    @MetaData(value = "已完成次数")
    private Integer checkedNum=0;

    @MetaData(value = "开始时间")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Date fromDate;

    @MetaData(value = "截止时间")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Date toDate;

    @MetaData(value = "启用停用")
    @Column(nullable = false)
    private Boolean enable = Boolean.FALSE;

    @Override
    public int compareTo(Advertising o) {
        return CompareToBuilder.reflectionCompare(o.getAdId(), this.getAdId());
    }

    @Override
    @Transient
    public String getDisplay() {
        return title;
    }

    @Transient
    public String getAdUrlAbstract() {
        if(adUrl !=null && !adUrl.equals("") && adUrl.length() > 40){
            return StringUtils.substring(ExtStringUtils.cutRedundanceStr(adUrl,40), 0, 40).trim() + "...";
        }else {
            return adUrl;
        }

    }
    @Transient
    public String getTitleAbstract() {
        if(title!=null && !title.equals("") && title.length() > 40)
            return StringUtils.substring(ExtStringUtils.cutRedundanceStr(title,40), 0, 40).trim() + "...";
        else
            return title;
    }
}
