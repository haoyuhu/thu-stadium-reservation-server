package com.huhaoyu.thu;

import com.huhaoyu.thu.widget.VisibleEntity;
import com.huhaoyu.thu.widget.VisibleField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by huhaoyu
 * Created On 2017/2/6 下午5:34.
 */

@Getter
@Setter
@VisibleEntity
public class AnnotationObject {

    @VisibleField(name = "test_date")
    private Date testDateWithAnnotation;

    @VisibleField(visible = false)
    protected Double useless;

    private Float testNoAnnotation;

    @VisibleField
    private Integer testNull;

    @VisibleField
    protected String stringValue;

    @VisibleField
    public long longValue;

    @VisibleField
    private List<TestObject> testList;

    @VisibleField
    protected Set<String> testSet;

    @VisibleField
    public Map<String, TestObject> testMap;

    @VisibleField
    private TestObject testFieldNameWithNumber2AtEnd;

}
