## 总体说明

对于企业应用具有一个功能强大的表格Grid组件，无论对于开发和用户体验来说，都能起到事半功倍的效果。经过多个项目经验积累，框架选择了目前主流的[![link](images/link.gif)JQuery jqGrid插件](http://www.trirand.com/blog/)。
该Grid组件功能非常丰富强大，除了一些分页、多字段排序等基础功能外，还包括诸如TreeGrid、SubGrid、AdvancedSearch、Group、ColumnChooser等众多高级特性，具体可参考其官方示例[![link](images/link.gif)jqGrid Demo](http://trirand.com/blog/jqgrid/jqgrid.html)。

框架层面一方面从前端对组件进行了封装增强，使开发过程只需简单基础配置定义即可拥有强大的表格组件功能；另一方面与Java后端进行充分整合，封装简化实现基本无需或很少量编码即可实现大量复杂常见的非功能需求功能：

* 简单灵活的分页、（多字段支持）排序
* 快速查询、高级组合嵌套查询
* 方便实用的主子表格处理
* 简化统一的Excel基础导出功能支持
* DND拖拽支持
* 功能强大的Inline Edit支持

## 典型功能介绍

以下对框架集成Grid组件的一些典型功能进行截图示意介绍，由于框架还在不断完善过程中，以下截图可能和最新版本有所差异，但基本不影响功能使用介绍。

### 快速过滤查询

组件上方的快速过滤查询区域，可以实现对列数据方便快速的过程查询，点击左侧小三角可以切换过滤条显示控制。
支持常见的三种输入类型：

* 文本框： 可以快速输入文本、数字等查询条件
* 下拉框：可以对于一些枚举类型数据以下拉框选取方式快速过滤查询
* 日期选取组件：日期类型数据快速选取查询

![grid-filter-toolbar](images/grid-filter-toolbar.gif)

除此，点击各查询输入组件左侧的操作符链接即可快速切换查询类型，各列会根据列数据类型或用户定义动态显示可选的查询类型：

![grid-filter-oper](images/grid-filter-oper.gif)

### 分页、排序

* 分页导航信息和操作区域
* 排序：支持多字段组合排序，根据点击先后和次数组合多字段排序

![grid-filter-pagesort](images/grid-filter-pagesort.gif)

### 主要操作按钮

在不同的功能页面，工具条操作按钮有所差异，包含一系列图标按钮、下拉列表、右键菜单等组合，具体含义可鼠标移动上方查看提示信息了解功能含义。

![grid-filter-buttons](images/grid-filter-buttons1.gif)
![grid-filter-buttons](images/grid-filter-buttons2.gif)
![grid-filter-buttons](images/grid-filter-buttons3.gif)
![grid-filter-buttons](images/grid-filter-buttons4.gif)
![grid-filter-buttons](images/grid-filter-buttons5.gif)

以下大致按照从左至右的顺序，介绍一下主要操作按钮：

* **下拉按钮组**： 提供一些基础功能和业务功能列表，与右键菜单基本是同步的；下拉按钮组列表一般会有个基本的分隔划分原则：无需选取的按钮一组，单选行项的业务功能一组，多选行项的业务功能一组。

* **常用功能按钮**：紧随下拉按钮组之后是一些常用功能按钮，可以定制化把一些常用的业务功能按钮从下拉按钮组挑选出来放置于此方便用户快速直接点击；

* **开启拖放移动模式**： 在一些如菜单的典型父子关系的表格页面，点击此按钮后可以鼠标自由拖拽形式调整各节点父子关系，非常方便。
目前限制：开启拖放模式后，则无法再进行常规的文本内容选取复制操作，需要重新刷新表格返回正常的编辑模式。

![grid-filter-dnd](images/grid-filter-dnd2.gif)

* **收缩显示模式**：一般情况下表格会自动计算各列合适宽度以刚好显示在表格数据区域，
但在表格列较多或显示宽度较窄的情况下，无法比较整齐的显示所有列数据，会自动以扩展模式显示出现横向滚动条，需要用户通过拉动横向滚动条查看数据；
但是如果用户还是希望以紧凑形式显示所有列数据，可以点击此按钮，表格将自动收缩显示数据。
目前限制:收缩后无法返回，需要整体刷新表格才能重新按照扩展模式显示。

![grid-filter-zoomin](images/grid-filter-zoomin.gif)
![grid-filter-zoomin](images/grid-filter-zoomin2.gif)

* **Excel下载按钮**：基于当前动态显示的列和行项数据导出Excel下载文件（注意：通用功能只实现当前页码表格数据导出，如果需要导出所有查询数据需要单独编码实现）；

![grid-filter-export](images/grid-filter-export.gif)

* **列选择器按钮**：点击弹出列选择器界面，可以实现对列顺序调整、显示/隐藏列控制等；

![grid-filter-columns](images/grid-filter-columns.gif)

* **查询按钮**：点击弹出高级组合查询窗口，在此窗口可实现复杂的嵌套组合条件查询；

![grid-filter-advsearch](images/grid-filter-advsearch.gif)

* **刷新按钮**：点击强制表格重新加载数据；

之后是几个功能强大的表格行项数据inline edit编辑按钮，可以实现快速高效的直接在表格组件中快速编辑行项数据保存：

![grid-filter-inlineedit](images/grid-filter-inlineedit.gif)

* **Inline Edit 添加 按钮**：如果表格没有选择任何一行项，则进入新增空白行项模式；如果已选中一行项，则直接复制选中行项进入新增数据模式；

* **Inline Edit 编辑 按钮**：选中一行项点击进入修改编辑模式或者在选中状态回车亦可进入编辑模式，编辑完成后点击按钮或回车保存后自动进入下一行编辑模式，便于快速连续编辑数据；

* **Inline Edit 保存 按钮**：编辑模式下，点击提交当前编辑数据，或键盘Enter回车同样效果；

* **Inline Edit 取消 按钮**：编辑模式下，点击取消当前编辑数据，或键盘ESC退出键同样效果；

* **查询按钮**： 最后一个是删除按钮，批量选取行项后点击此按钮提交批量删除请求；

### Master-Detail主子表数据交互处理

一般业务系统一种常见的数据结构就是诸如销售单之类的master-detail（主子表/主从表/抬头明细表等叫法）数据结构和表单形式，框架提供一个以销售单编辑界面为例展示此场景处理：

![grid-master-detail](images/grid-master-detail.png)

### 其他特性

具体可访问示例应用体验这些特性效果。

* 表格宽度、高度的自适应
* 表格Resize支持（双击下边缘自动伸展表格）
* 奇偶行颜色交替
* 表头宽度调整
* 表头列顺序调整
* 双击行项事件
* 。。。不少令人惊喜的细节的处理，敬请不断发现体验
