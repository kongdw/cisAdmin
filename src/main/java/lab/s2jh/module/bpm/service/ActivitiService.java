package lab.s2jh.module.bpm.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lab.s2jh.core.security.AuthContextHolder;
import lab.s2jh.module.bpm.BpmTrackable;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
@Transactional
public class ActivitiService {

    private final static Logger logger = LoggerFactory.getLogger(ActivitiService.class);

    public static final String BPM_ENTITY_VAR_NAME = "entity";

    public static final String BPM_INITIATOR_VAR_NAME = "initiator";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired(required = false)
    private RuntimeService runtimeService;

    @Autowired(required = false)
    private TaskService taskService;

    @Autowired(required = false)
    private FormService formService;

    @Autowired(required = false)
    private RepositoryService repositoryService;

    @Autowired(required = false)
    private IdentityService identityService;

    @Autowired(required = false)
    private HistoryService historyService;

    @Autowired(required = false)
    private ProcessEngineFactoryBean processEngine;

    /**
     * 基于业务主键构建流程实例运行图
     * @param bizKey
     * @return
     */
    public InputStream buildProcessImageByBizKey(String bizKey) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bizKey).singleResult();
        return buildProcessImageByProcessInstance(processInstance);
    }

    /**
     * 基于ProcessInstanceId删除流程
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByProcessInstanceId(String processInstanceId, String message) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        deleteProcessInstanceByProcessInstance(processInstance, message);
    }

    /**
     * 基于业务主键删除流程
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByEntity(BpmTrackable entity) {
        entity.setActiveTaskName("END");
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(entity.getBpmBusinessKey())
                .singleResult();
        deleteProcessInstanceByProcessInstance(processInstance, "Casecade by entity delete [" + entity.getBpmBusinessKey() + "]");
    }

    /**
     * 基于业务主键删除流程
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByBizKey(String bizKey, String message) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bizKey).singleResult();
        deleteProcessInstanceByProcessInstance(processInstance, message);
    }

    /**
     * 基于processInstance删除流程
     * @param bizKey
     * @return
     */
    public void deleteProcessInstanceByProcessInstance(ProcessInstance processInstance, String message) {
        if (processInstance != null) {
            //try-catch处理，避免由于更新版本导致遗留的脏数据获取对象处理失败导致无法删除
            try {
                Object val = runtimeService.getVariable(processInstance.getProcessInstanceId(), BPM_ENTITY_VAR_NAME);
                if (val != null && val instanceof BpmTrackable) {
                    BpmTrackable entity = (BpmTrackable) val;
                    entity.setActiveTaskName("END");
                    entityManager.persist(entity);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            identityService.setAuthenticatedUserId(AuthContextHolder.getAuthSysUserUid());
            runtimeService.deleteProcessInstance(processInstance.getId(), message);
        }
    }

    /**
     * 基于流程实例ID构建流程实例运行图
     * @param processInstanceId
     * @return
     */
    public InputStream buildProcessImageByProcessInstanceId(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        return buildProcessImageByProcessInstance(processInstance);
    }

    private InputStream buildProcessImageByProcessInstance(ProcessInstance processInstance) {
        if (processInstance == null) {
            return null;
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance
                .getProcessDefinitionId());
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstance.getProcessInstanceId());
        // 使用spring注入引擎请使用下面的这行代码
        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

        List<String> highLightedFlows = getHighLightedFlows(processDefinition, processInstance.getProcessInstanceId());

        InputStream imageStream = processEngine
                .getProcessEngineConfiguration()
                .getProcessDiagramGenerator()
                .generateDiagram(bpmnModel, "png", activeActivityIds, highLightedFlows,
                        processEngine.getProcessEngineConfiguration().getActivityFontName(),
                        processEngine.getProcessEngineConfiguration().getLabelFontName(),
                        processEngine.getProcessEngineConfiguration().getClassLoader(), 1.0);

        return imageStream;
    }

    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinition, String processInstanceId) {
        List<String> historicActivityInstanceList = new ArrayList<String>();
        List<String> highLightedFlows = new ArrayList<String>();

        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

        for (HistoricActivityInstance hai : historicActivityInstances) {
            historicActivityInstanceList.add(hai.getActivityId());
        }

        // add current activities to list
        List<String> highLightedActivities = runtimeService.getActiveActivityIds(processInstanceId);
        historicActivityInstanceList.addAll(highLightedActivities);

        // activities and their sequence-flows
        getHighLightedFlows(processDefinition.getActivities(), highLightedFlows, historicActivityInstanceList);

        return highLightedFlows;
    }

    private void getHighLightedFlows(List<ActivityImpl> activityList, List<String> highLightedFlows, List<String> historicActivityInstanceList) {
        for (ActivityImpl activity : activityList) {
            if (activity.getProperty("type").equals("subProcess")) {
                // get flows for the subProcess
                getHighLightedFlows(activity.getActivities(), highLightedFlows, historicActivityInstanceList);
            }

            if (historicActivityInstanceList.contains(activity.getId())) {
                List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();
                for (PvmTransition pvmTransition : pvmTransitionList) {
                    String destinationFlowId = pvmTransition.getDestination().getId();
                    if (historicActivityInstanceList.contains(destinationFlowId)) {
                        highLightedFlows.add(pvmTransition.getId());
                    }
                }
            }
        }
    }

    /**
     * 基于业务主键查询正在运行的流程实例
     * @param entity 流程业务对象
     * @return
     */
    public ProcessInstance findRunningProcessInstance(BpmTrackable entity) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(entity.getBpmBusinessKey())
                .singleResult();
        return processInstance;
    }

    /**
     * 查询业务对象当前活动任务名称
     * @param bizKey 启动流程的业务主键
     * @return
     */
    public String findActiveTaskNames(String bizKey) {
        Assert.notNull(bizKey);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(bizKey).singleResult();
        //流程已完结，直接返回null
        if (processInstance == null) {
            return "END";
        }
        List<String> ids = runtimeService.getActiveActivityIds(processInstance.getId());
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
        List<ActivityImpl> activityImpls = pde.getActivities();
        List<String> activeActs = Lists.newArrayList();
        for (ActivityImpl activityImpl : activityImpls) {
            if (ids.contains(activityImpl.getId())) {
                activeActs.add(ObjectUtils.toString(activityImpl.getProperty("name")));
            }
        }
        return StringUtils.join(activeActs, ",");
    }

    /**  
     * 根据当前任务ID，查询可以驳回的任务节点  
     *   
     * @param taskId 当前任务ID  
     */
    public List<ActivityImpl> findBackActivities(String taskId) {
        List<ActivityImpl> rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId, null), new ArrayList<ActivityImpl>(),
                new ArrayList<ActivityImpl>());
        return reverList(rtnList);
    }

    /**  
     * 返回指定目标活动节点  
     *   
     * @param taskId 当前任务ID  
     * @param activityId 返回节点活动ID  
     * @param variables 流程存储参数  
     * @throws Exception  
     */
    public void backActivity(String taskId, String activityId, Map<String, Object> variables) {
        Assert.notNull(activityId, "Back target process activity id required");

        // 查找所有并行任务节点，同时驳回    
        List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
        for (Task task : taskList) {
            commitProcess(task.getId(), variables, activityId);
        }
    }

    /**  
     * 返回指定目标活动节点  
     *   
     * @param taskId 当前任务ID  
     * @param activityId 返回节点活动ID  
     * @throws Exception  
     */
    public void backActivity(String taskId, String activityId) {
        backActivity(taskId, activityId, null);
    }

    /**  
     * 清空指定活动节点流向  
     *   
     * @param activityImpl 活动节点  
     * @return 节点流向集合  
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // 存储当前节点所有流向临时变量    
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        // 获取当前节点所有流向，存储到临时变量，然后清空    
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**  
     * @param taskId 当前任务ID  
     * @param variables 流程变量  
     * @param activityId 流程转向执行任务节点ID<br>此参数为空，默认为提交操作  
     * @throws Exception  
     */
    private void commitProcess(String taskId, Map<String, Object> variables, String activityId) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        // 跳转节点为空，默认提交操作    
        if (StringUtils.isEmpty(activityId)) {
            taskService.complete(taskId, variables);
        } else {// 流程转向操作    
            turnTransition(taskId, activityId, variables);
        }
    }

    /**  
     * 中止流程(特权人直接审批通过等)  
     *   
     * @param taskId  
     */
    public void endProcess(String taskId) {
        ActivityImpl endActivity = findActivitiImpl(taskId, "end");
        commitProcess(taskId, null, endActivity.getId());
    }

    /**  
     * 根据流入任务集合，查询最近一次的流入任务节点  
     *   
     * @param processInstance 流程实例  
     * @param tempList 流入任务集合  
     * @return  
     */
    private ActivityImpl filterNewestActivity(ProcessInstance processInstance, List<ActivityImpl> tempList) {
        while (tempList.size() > 0) {
            ActivityImpl activity_1 = tempList.get(0);
            HistoricActivityInstance activityInstance_1 = findHistoricUserTask(processInstance, activity_1.getId());
            if (activityInstance_1 == null) {
                tempList.remove(activity_1);
                continue;
            }

            if (tempList.size() > 1) {
                ActivityImpl activity_2 = tempList.get(1);
                HistoricActivityInstance activityInstance_2 = findHistoricUserTask(processInstance, activity_2.getId());
                if (activityInstance_2 == null) {
                    tempList.remove(activity_2);
                    continue;
                }

                if (activityInstance_1.getEndTime().before(activityInstance_2.getEndTime())) {
                    tempList.remove(activity_1);
                } else {
                    tempList.remove(activity_2);
                }
            } else {
                break;
            }
        }
        if (tempList.size() > 0) {
            return tempList.get(0);
        }
        return null;
    }

    /**  
     * 根据任务ID和节点ID获取活动节点 <br>  
     *   
     * @param taskId 任务ID  
     * @param activityId 活动节点ID<br>如果为null或""，则默认查询当前活动节点 <br>如果为"end"，则查询结束节点 <br>  
     *   
     * @return  
     * @throws Exception  
     */
    private ActivityImpl findActivitiImpl(String taskId, String activityId) {
        // 取得流程定义    
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

        // 获取当前活动节点ID    
        if (StringUtils.isEmpty(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }

        // 根据流程定义，获取该流程实例的结束节点    
        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }

        // 根据节点ID，获取对应的活动节点    
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition).findActivity(activityId);

        return activityImpl;
    }

    /**  
     * 查询指定任务节点的最新记录  
     *   
     * @param processInstance 流程实例  
     * @param activityId  
     * @return  
     */
    private HistoricActivityInstance findHistoricUserTask(ProcessInstance processInstance, String activityId) {
        HistoricActivityInstance rtnVal = null;
        // 查询当前流程实例审批结束的历史节点    
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId()).activityId(activityId).finished().orderByHistoricActivityInstanceEndTime().desc().list();
        if (historicActivityInstances.size() > 0) {
            rtnVal = historicActivityInstances.get(0);
        }

        return rtnVal;
    }

    /**  
     * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID  
     *   
     * @param activityImpl 当前节点  
     * @return  
     */
    private String findParallelGatewayId(ActivityImpl activityImpl) {
        List<PvmTransition> incomingTransitions = activityImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            activityImpl = transitionImpl.getDestination();
            String type = (String) activityImpl.getProperty("type");
            if ("parallelGateway".equals(type)) {// 并行路线    
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
                if ("END".equals(gatewayType.toUpperCase())) {
                    return gatewayId.substring(0, gatewayId.lastIndexOf("_")) + "_start";
                }
            }
        }
        return null;
    }

    /**  
     * 根据任务ID获取流程定义  
     *   
     * @param taskId  
     *            任务ID  
     * @return  
     * @throws Exception  
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) {
        // 取得流程定义    
        return (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(findTaskById(taskId)
                .getProcessDefinitionId());
    }

    /**  
     * 根据任务ID获取对应的流程实例  
     *   
     * @param taskId 任务ID  
     * @return  
     * @throws Exception  
     */
    private ProcessInstance findProcessInstanceByTaskId(String taskId) {
        // 找到流程实例    
        return runtimeService.createProcessInstanceQuery().processInstanceId(findTaskById(taskId).getProcessInstanceId()).singleResult();
    }

    /**  
     * 根据任务ID获得任务实例  
     *   
     * @param taskId 任务ID  
     * @return  
     * @throws Exception  
     */
    private TaskEntity findTaskById(String taskId) {
        return (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**  
     * 根据流程实例ID和任务key值查询所有同级任务集合  
     *   
     * @param processInstanceId  
     * @param key  
     * @return  
     */
    private List<Task> findTaskListByKey(String processInstanceId, String key) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(key).list();
    }

    /**  
     * 迭代循环流程树结构，查询当前节点可驳回的任务节点  
     *   
     * @param taskId 当前任务ID  
     * @param currActivity 当前活动节点  
     * @param rtnList 存储回退节点集合  
     * @param tempList 临时存储节点集合（存储一次迭代过程中的同级userTask节点）  
     * @return 回退节点集合  
     */
    private List<ActivityImpl> iteratorBackActivity(String taskId, ActivityImpl currActivity, List<ActivityImpl> rtnList, List<ActivityImpl> tempList) {
        // 查询流程定义，生成流程树结构    
        ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

        // 当前节点的流入来源    
        List<PvmTransition> incomingTransitions = currActivity.getIncomingTransitions();
        // 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点    
        List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
        // 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点    
        List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
        // 遍历当前节点所有流入路径    
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");
            /**  
             * 并行节点配置要求：<br>  
             * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)  
             */
            if ("parallelGateway".equals(type)) {// 并行路线    
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
                if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归    
                    return rtnList;
                } else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点    
                    parallelGateways.add(activityImpl);
                }
            } else if ("startEvent".equals(type)) {// 开始节点，停止递归    
                return rtnList;
            } else if ("userTask".equals(type)) {// 用户任务    
                tempList.add(activityImpl);
            } else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点    
                currActivity = transitionImpl.getSource();
                exclusiveGateways.add(currActivity);
            }
        }

        /**  
         * 迭代条件分支集合，查询对应的userTask节点  
         */
        for (ActivityImpl activityImpl : exclusiveGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**  
         * 迭代并行集合，查询对应的userTask节点  
         */
        for (ActivityImpl activityImpl : parallelGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**  
         * 根据同级userTask集合，过滤最近发生的节点  
         */
        currActivity = filterNewestActivity(processInstance, tempList);
        if (currActivity != null) {
            // 查询当前节点的流向是否为并行终点，并获取并行起点ID    
            String id = findParallelGatewayId(currActivity);
            if (StringUtils.isEmpty(id)) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点    
                rtnList.add(currActivity);
            } else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点    
                currActivity = findActivitiImpl(taskId, id);
            }

            // 清空本次迭代临时集合    
            tempList.clear();
            // 执行下次迭代    
            iteratorBackActivity(taskId, currActivity, rtnList, tempList);
        }
        return rtnList;
    }

    /**  
     * 还原指定活动节点流向  
     *   
     * @param activityImpl 活动节点  
     * @param oriPvmTransitionList 原有节点流向集合  
     */
    private void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向    
        List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向    
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    /**  
     * 反向排序list集合，便于驳回节点按顺序显示  
     *   
     * @param list  
     * @return  
     */
    private List<ActivityImpl> reverList(List<ActivityImpl> list) {
        List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
        // 由于迭代出现重复数据，排除重复    
        for (int i = list.size(); i > 0; i--) {
            if (!rtnList.contains(list.get(i - 1)))
                rtnList.add(list.get(i - 1));
        }
        return rtnList;
    }

    /**  
     * 流程转向操作  
     *   
     * @param taskId 当前任务ID  
     * @param activityId 目标节点任务ID  
     * @param variables 流程变量  
     * @throws Exception  
     */
    private void turnTransition(String taskId, String activityId, Map<String, Object> variables) {
        // 当前节点    
        ActivityImpl currActivity = findActivitiImpl(taskId, null);
        // 清空当前流向    
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // 创建新流向    
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        // 目标节点    
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
        // 设置新流向的目标节点    
        newTransition.setDestination(pointActivity);

        // 执行转向任务    
        taskService.complete(taskId, variables);
        // 删除目标节点新流入    
        pointActivity.getIncomingTransitions().remove(newTransition);

        // 还原以前流向    
        restoreTransition(currActivity, oriPvmTransitionList);
    }

    /**
     * 按照流程定义Key启动最新版本流程实例
     * @param processDefinitionKey
     * @param businessKey
     * @param variables
     * @return
     */
    public void startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthSysUserUid());
        runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    /**
     * 按照流程定义Key启动最新版本流程实例
     * @param processDefinitionKey
     * @param businessKey
     * @param entity
     * @return
     */
    public void startProcessInstanceByKey(String processDefinitionKey, BpmTrackable entity) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthSysUserUid());
        Map variables = Maps.newHashMap();
        //追加当前实体对象追加到流程变量
        variables.put(BPM_ENTITY_VAR_NAME, entity);
        runtimeService.startProcessInstanceByKey(processDefinitionKey, entity.getBpmBusinessKey(), variables);
        String activeTaskNames = findActiveTaskNames(entity.getBpmBusinessKey());
        entity.setActiveTaskName(activeTaskNames);
    }

    /**
     * 完成任务
     * @param taskId
     * @param variables
     * @return
     */
    public void completeTask(String taskId, Map<String, Object> variables) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthSysUserUid());
        if (variables != null && variables.size() > 0) {
            taskService.setVariablesLocal(taskId, variables);
        }
        BpmTrackable entity = (BpmTrackable) taskService.getVariable(taskId, BPM_ENTITY_VAR_NAME);
        taskService.complete(taskId, variables);
        if (entity != null) {
            entity = entityManager.find(entity.getClass(), entity.getId());
            String activeTaskNames = findActiveTaskNames(entity.getBpmBusinessKey());
            entity.setActiveTaskName(activeTaskNames);
            entityManager.persist(entity);
        }
    }

    /**
     * 基于表单数据完成任务
     * @param taskId
     * @param formProperties
     * @return
     */
    public void submitTaskFormData(String taskId, Map<String, String> formProperties) {
        identityService.setAuthenticatedUserId(AuthContextHolder.getAuthSysUserUid());
        BpmTrackable entity = (BpmTrackable) taskService.getVariable(taskId, BPM_ENTITY_VAR_NAME);
        formService.submitTaskFormData(taskId, formProperties);
        if (entity != null) {
            entity = entityManager.find(entity.getClass(), entity.getId());
            String activeTaskNames = findActiveTaskNames(entity.getBpmBusinessKey());
            entity.setActiveTaskName(activeTaskNames);
            entityManager.persist(entity);
        }
    }

    /**
     * 查询用户指定任务
     * @param authenticatedUserId 登录账号，AuthContextHolder.getAuthSysUserUid()
     * @param bizKey 业务键
     * @param taskDefinitionKey Task定义的ID Key
     * @return 
     */
    public Task findTask(String authenticatedUserId, String bizKey, String taskDefinitionKey) {
        return taskService.createTaskQuery().taskAssignee(authenticatedUserId).processInstanceBusinessKey(bizKey)
                .taskDefinitionKey(taskDefinitionKey).singleResult();
    }
}
