# 什么是CMM？

## CMM的定义

CMM（Capability Maturity Model，能力成熟度模型）是由美国卡内基梅隆大学软件工程研究所（SEI）在20世纪80年代末开发的一种软件过程改进模型。CMM旨在帮助软件开发组织改进其软件开发过程，提高软件开发的质量和生产效率。

CMM是一个五级的成熟度模型，每个级别都代表软件开发过程的不同成熟度水平。CMM通过评估组织的软件开发过程，帮助组织识别其当前的能力水平，并提供改进建议。

## CMM的历史背景

### 1. CMM的起源

CMM的起源可以追溯到20世纪80年代。当时，美国国防部（DoD）发现软件项目的失败率很高，主要原因是软件开发过程不成熟。为了解决这个问题，美国国防部委托卡内基梅隆大学软件工程研究所（SEI）开发一种评估和改进软件开发过程的方法。

### 2. CMM的发展

CMM的发展经历了几个阶段：

- **1987年**：SEI发布了第一个CMM版本
- **1991年**：SEI发布了CMM 1.0版本
- **1993年**：SEI发布了CMM 1.1版本
- **2000年**：SEI发布了CMMI（Capability Maturity Model Integration，能力成熟度模型集成），将CMM与其他能力成熟度模型集成

### 3. CMM的影响

CMM对软件工程领域产生了深远的影响：

- **软件过程改进**：CMM推动了软件过程改进运动
- **质量保证**：CMM提高了软件质量保证的水平
- **项目管理**：CMM改进了软件项目管理的方法
- **国际标准**：CMM成为国际软件过程改进的标准

## CMM的五个成熟度级别

CMM定义了五个成熟度级别，每个级别都代表软件开发过程的不同成熟度水平。

### 1. 初始级（Level 1: Initial）

#### 定义

初始级是CMM的最低级别。在初始级，软件开发过程是混乱的，缺乏一致性和可预测性。项目的成功主要依赖于个人的能力和英雄行为，而不是组织的过程能力。

#### 特征

- **过程混乱**：软件开发过程是混乱的，缺乏一致性
- **依赖个人**：项目的成功主要依赖于个人的能力
- **不可预测**：项目的结果不可预测，经常超出预算和延期
- **缺乏文档**：缺乏文档和过程记录
- **反应式管理**：管理是反应式的，而不是主动的

#### 示例

```java
// 初始级的软件开发过程示例

class Project {
    public void develop() {
        // 没有明确的开发过程
        // 开发人员按照自己的方式开发
        // 没有文档和记录
        // 项目进度不可预测
    }
}
```

### 2. 可重复级（Level 2: Repeatable）

#### 定义

可重复级是CMM的第二级别。在可重复级，组织已经建立了基本的项目管理过程，可以重复以前成功项目的实践。项目的成功不再完全依赖于个人的能力，而是依赖于组织的过程能力。

#### 特征

- **基本过程**：建立了基本的项目管理过程
- **可重复**：可以重复以前成功项目的实践
- **需求管理**：建立了需求管理过程
- **项目计划**：建立了项目计划过程
- **质量保证**：建立了基本的质量保证过程

#### 示例

```java
// 可重复级的软件开发过程示例

class Project {
    private RequirementManagement requirementManagement;
    private ProjectPlanning projectPlanning;
    private QualityAssurance qualityAssurance;
    
    public Project() {
        this.requirementManagement = new RequirementManagement();
        this.projectPlanning = new ProjectPlanning();
        this.qualityAssurance = new QualityAssurance();
    }
    
    public void develop() {
        requirementManagement.manage();
        projectPlanning.plan();
        qualityAssurance.assure();
    }
}

class RequirementManagement {
    public void manage() {
        System.out.println("管理需求");
    }
}

class ProjectPlanning {
    public void plan() {
        System.out.println("制定项目计划");
    }
}

class QualityAssurance {
    public void assure() {
        System.out.println("质量保证");
    }
}
```

### 3. 已定义级（Level 3: Defined）

#### 定义

已定义级是CMM的第三级别。在已定义级，组织已经建立了标准的软件开发过程，所有项目都使用这个标准过程。组织还建立了过程改进的机制，可以持续改进软件开发过程。

#### 特征

- **标准过程**：建立了标准的软件开发过程
- **过程文档**：过程文档完整且一致
- **培训**：对开发人员进行过程培训
- **过程改进**：建立了过程改进机制
- **度量**：建立了过程度量机制

#### 示例

```java
// 已定义级的软件开发过程示例

class Organization {
    private StandardProcess standardProcess;
    private ProcessTraining processTraining;
    private ProcessImprovement processImprovement;
    private ProcessMeasurement processMeasurement;
    
    public Organization() {
        this.standardProcess = new StandardProcess();
        this.processTraining = new ProcessTraining();
        this.processImprovement = new ProcessImprovement();
        this.processMeasurement = new ProcessMeasurement();
    }
    
    public void manage() {
        standardProcess.define();
        processTraining.train();
        processImprovement.improve();
        processMeasurement.measure();
    }
}

class StandardProcess {
    public void define() {
        System.out.println("定义标准过程");
    }
}

class ProcessTraining {
    public void train() {
        System.out.println("培训开发人员");
    }
}

class ProcessImprovement {
    public void improve() {
        System.out.println("改进过程");
    }
}

class ProcessMeasurement {
    public void measure() {
        System.out.println("度量过程");
    }
}
```

### 4. 已管理级（Level 4: Managed）

#### 定义

已管理级是CMM的第四级别。在已管理级，组织已经建立了度量和分析机制，可以定量地管理和控制软件开发过程。组织可以预测软件开发过程的性能，并采取相应的措施来改进过程。

#### 特征

- **定量管理**：建立了定量管理机制
- **过程度量**：建立了过程度量机制
- **质量度量**：建立了质量度量机制
- **过程控制**：建立了过程控制机制
- **过程预测**：可以预测过程性能

#### 示例

```java
// 已管理级的软件开发过程示例

class Organization {
    private QuantitativeManagement quantitativeManagement;
    private ProcessMetrics processMetrics;
    private QualityMetrics qualityMetrics;
    private ProcessControl processControl;
    private ProcessPrediction processPrediction;
    
    public Organization() {
        this.quantitativeManagement = new QuantitativeManagement();
        this.processMetrics = new ProcessMetrics();
        this.qualityMetrics = new QualityMetrics();
        this.processControl = new ProcessControl();
        this.processPrediction = new ProcessPrediction();
    }
    
    public void manage() {
        quantitativeManagement.manage();
        processMetrics.measure();
        qualityMetrics.measure();
        processControl.control();
        processPrediction.predict();
    }
}

class QuantitativeManagement {
    public void manage() {
        System.out.println("定量管理");
    }
}

class ProcessMetrics {
    public void measure() {
        System.out.println("度量过程");
    }
}

class QualityMetrics {
    public void measure() {
        System.out.println("度量质量");
    }
}

class ProcessControl {
    public void control() {
        System.out.println("控制过程");
    }
}

class ProcessPrediction {
    public void predict() {
        System.out.println("预测过程性能");
    }
}
```

### 5. 优化级（Level 5: Optimizing）

#### 定义

优化级是CMM的最高级别。在优化级，组织已经建立了持续改进的机制，可以持续改进软件开发过程。组织可以主动地识别和消除软件开发过程中的缺陷，不断提高软件开发的质量和生产效率。

#### 特征

- **持续改进**：建立了持续改进机制
- **缺陷预防**：建立了缺陷预防机制
- **技术革新**：积极采用新技术和新方法
- **过程优化**：持续优化软件开发过程
- **创新文化**：建立了创新文化

#### 示例

```java
// 优化级的软件开发过程示例

class Organization {
    private ContinuousImprovement continuousImprovement;
    private DefectPrevention defectPrevention;
    private TechnologyInnovation technologyInnovation;
    private ProcessOptimization processOptimization;
    private InnovationCulture innovationCulture;
    
    public Organization() {
        this.continuousImprovement = new ContinuousImprovement();
        this.defectPrevention = new DefectPrevention();
        this.technologyInnovation = new TechnologyInnovation();
        this.processOptimization = new ProcessOptimization();
        this.innovationCulture = new InnovationCulture();
    }
    
    public void manage() {
        continuousImprovement.improve();
        defectPrevention.prevent();
        technologyInnovation.innovate();
        processOptimization.optimize();
        innovationCulture.build();
    }
}

class ContinuousImprovement {
    public void improve() {
        System.out.println("持续改进");
    }
}

class DefectPrevention {
    public void prevent() {
        System.out.println("预防缺陷");
    }
}

class TechnologyInnovation {
    public void innovate() {
        System.out.println("技术革新");
    }
}

class ProcessOptimization {
    public void optimize() {
        System.out.println("优化过程");
    }
}

class InnovationCulture {
    public void build() {
        System.out.println("建立创新文化");
    }
}
```

## CMM的关键过程域

CMM定义了18个关键过程域（Key Process Areas，KPAs），这些关键过程域是实现CMM成熟度级别的关键。

### 1. 可重复级的关键过程域

可重复级包含6个关键过程域：

- **需求管理**（Requirements Management）：管理需求，确保需求的一致性和可追溯性
- **项目计划**（Project Planning）：制定项目计划，包括进度、资源和风险管理
- **项目跟踪**（Project Tracking）：跟踪项目进度，及时发现和解决问题
- **子合同管理**（Subcontract Management）：管理子合同，确保子合同的质量和进度
- **质量保证**（Quality Assurance）：建立质量保证过程，确保软件质量
- **配置管理**（Configuration Management）：建立配置管理过程，管理软件配置

### 2. 已定义级的关键过程域

已定义级包含7个关键过程域：

- **组织过程焦点**（Organization Process Focus）：建立组织过程改进的焦点
- **组织过程定义**（Organization Process Definition）：定义组织标准过程
- **培训计划**（Training Program）：制定培训计划，提高开发人员的能力
- **集成软件管理**（Integrated Software Management）：集成软件管理过程
- **软件产品工程**（Software Product Engineering）：建立软件产品工程过程
- **组间协调**（Intergroup Coordination）：协调不同组之间的工作
- **同行评审**（Peer Reviews）：建立同行评审过程，提高软件质量

### 3. 已管理级的关键过程域

已管理级包含2个关键过程域：

- **定量过程管理**（Quantitative Process Management）：建立定量过程管理机制
- **软件质量管理**（Software Quality Management）：建立软件质量管理机制

### 4. 优化级的关键过程域

优化级包含3个关键过程域：

- **缺陷预防**（Defect Prevention）：建立缺陷预防机制
- **技术革新**（Technology Change Management）：建立技术革新机制
- **过程变更管理**（Process Change Management）：建立过程变更管理机制

## CMM的评估方法

CMM提供了两种评估方法：CBA-IPI（CMM-Based Appraisal for Internal Process Improvement）和SCAMPI（Standard CMMI Appraisal Method for Process Improvement）。

### 1. CBA-IPI

CBA-IPI是CMM的内部过程改进评估方法，主要用于组织内部的自我评估。

#### CBA-IPI的特点

- **内部评估**：主要用于组织内部的自我评估
- **过程改进**：主要用于过程改进
- **灵活性高**：评估方法灵活，可以根据组织的需求调整

#### CBA-IPI的步骤

1. **准备阶段**：准备评估计划，确定评估范围和目标
2. **数据收集**：收集过程数据，包括文档、访谈和观察
3. **数据分析**：分析过程数据，识别过程的优势和劣势
4. **评估报告**：编写评估报告，提供改进建议

### 2. SCAMPI

SCAMPI是CMMI的标准评估方法，既可以用于组织内部的自我评估，也可以用于外部评估。

#### SCAMPI的特点

- **标准评估**：是CMMI的标准评估方法
- **内外评估**：既可以用于内部评估，也可以用于外部评估
- **权威性高**：评估结果具有权威性

#### SCAMPI的步骤

1. **准备阶段**：准备评估计划，确定评估范围和目标
2. **数据收集**：收集过程数据，包括文档、访谈和观察
3. **数据分析**：分析过程数据，识别过程的优势和劣势
4. **评估报告**：编写评估报告，提供改进建议
5. **评估结果**：提供评估结果，包括成熟度级别

## CMM的优缺点

### 1. 优点

- **过程改进**：CMM提供了系统化的过程改进方法
- **质量保证**：CMM提高了软件质量保证的水平
- **项目管理**：CMM改进了软件项目管理的方法
- **可预测性**：CMM提高了项目结果的可预测性
- **国际标准**：CMM成为国际软件过程改进的标准

### 2. 缺点

- **成本高**：实施CMM需要投入大量的人力、物力和财力
- **时间长**：实施CMM需要较长的时间
- **灵活性低**：CMM的过程较为僵化，灵活性较低
- **文档繁重**：CMM需要大量的文档，文档负担较重
- **文化冲突**：CMM可能与组织文化产生冲突

## CMM的应用

### 1. 软件开发组织

CMM广泛应用于软件开发组织：

- **大型软件公司**：如 IBM、Microsoft、Oracle 等
- **中小型软件公司**：通过CMM提高软件开发能力
- **政府机构**：如美国国防部、NASA 等

### 2. 软件项目

CMM应用于软件项目：

- **大型软件项目**：如操作系统、数据库管理系统等
- **中小型软件项目**：通过CMM提高项目成功率
- **关键软件项目**：如航空、航天、医疗等领域的软件项目

### 3. 软件外包

CMM应用于软件外包：

- **外包评估**：通过CMM评估外包供应商的能力
- **外包管理**：通过CMM管理外包项目
- **外包质量**：通过CMM提高外包项目的质量

## CMM的局限性

### 1. 过于僵化

CMM的过程较为僵化，灵活性较低：

- **一刀切**：CMM对不同的组织和项目采用相同的标准
- **缺乏灵活性**：CMM缺乏灵活性，难以适应不同的组织和项目
- **创新受限**：CMM可能限制创新和创造力

### 2. 文档繁重

CMM需要大量的文档，文档负担较重：

- **文档成本**：编写和维护文档需要投入大量的人力、物力和财力
- **文档质量**：文档质量参差不齐，可能影响文档的有效性
- **文档更新**：文档更新不及时，可能影响文档的准确性

### 3. 成本高昂

实施CMM需要投入大量的人力、物力和财力：

- **人力成本**：需要投入大量的人力来实施CMM
- **物力成本**：需要投入大量的物力来支持CMM的实施
- **财力成本**：需要投入大量的财力来购买CMM相关的工具和培训

### 4. 文化冲突

CMM可能与组织文化产生冲突：

- **文化差异**：CMM可能与组织文化产生冲突
- **抵触情绪**：开发人员可能对CMM产生抵触情绪
- **实施困难**：CMM的实施可能遇到困难

## CMM的发展趋势

### 1. CMMI

CMMI（Capability Maturity Model Integration，能力成熟度模型集成）是CMM的继承和发展：

- **集成多个模型**：CMMI集成了多个能力成熟度模型
- **扩展应用领域**：CMMI扩展了应用领域，不仅限于软件开发
- **提高灵活性**：CMMI提高了灵活性，适应不同的组织和项目

### 2. 敏捷CMMI

敏捷CMMI是CMMI与敏捷方法的结合：

- **敏捷方法**：结合敏捷方法，提高灵活性
- **CMMI原则**：保持CMMI的核心原则
- **平衡发展**：平衡灵活性和规范性

### 3. DevOps

DevOps是CMM与DevOps的结合：

- **DevOps方法**：结合DevOps方法，提高效率
- **CMM原则**：保持CMM的核心原则
- **持续改进**：强调持续改进和持续交付

## 总结

CMM（Capability Maturity Model，能力成熟度模型）是由美国卡内基梅隆大学软件工程研究所（SEI）在20世纪80年代末开发的一种软件过程改进模型。CMM旨在帮助软件开发组织改进其软件开发过程，提高软件开发的质量和生产效率。CMM是一个五级的成熟度模型，包括初始级、可重复级、已定义级、已管理级和优化级。CMM定义了18个关键过程域，这些关键过程域是实现CMM成熟度级别的关键。CMM提供了两种评估方法：CBA-IPI和SCAMPI。CMM的优点包括过程改进、质量保证、项目管理、可预测性和国际标准，缺点包括成本高、时间长、灵活性低、文档繁重和文化冲突。CMM广泛应用于软件开发组织、软件项目和软件外包。CMM的局限性包括过于僵化、文档繁重、成本高昂和文化冲突。CMM的发展趋势包括CMMI、敏捷CMMI和DevOps。