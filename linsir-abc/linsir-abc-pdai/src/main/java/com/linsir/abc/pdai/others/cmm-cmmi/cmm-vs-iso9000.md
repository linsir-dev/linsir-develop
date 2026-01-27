# CMM与ISO9000的主要区别？

## 概述

CMM（Capability Maturity Model，能力成熟度模型）和ISO 9000是两个不同的质量管理体系。CMM是由美国卡内基梅隆大学软件工程研究所（SEI）开发的软件过程改进模型，专注于软件开发过程。ISO 9000是由国际标准化组织（ISO）制定的质量管理体系标准，适用于各种行业和领域。CMM和ISO 9000在定义、历史背景、应用领域、评估方法、优缺点等方面都有显著的区别。

## CMM与ISO9000的定义对比

### 1. CMM的定义

CMM（Capability Maturity Model，能力成熟度模型）是由美国卡内基梅隆大学软件工程研究所（SEI）在20世纪80年代末开发的一种软件过程改进模型。CMM旨在帮助软件开发组织改进其软件开发过程，提高软件开发的质量和生产效率。

#### CMM的特点

- **专注于软件**：CMM专注于软件开发过程
- **五级模型**：CMM是一个五级的成熟度模型
- **18个关键过程域**：CMM定义了18个关键过程域
- **软件工程**：CMM主要应用于软件工程领域
- **过程改进**：CMM旨在帮助组织改进软件开发过程

#### CMM的核心思想

CMM的核心思想是通过评估组织的软件开发过程，帮助组织识别其当前的能力水平，并提供改进建议。CMM通过五个成熟度级别来描述组织的软件开发过程能力，每个级别都代表软件开发过程的不同成熟度水平。

### 2. ISO9000的定义

ISO 9000是由国际标准化组织（ISO）制定的质量管理体系标准，包括ISO 9000、ISO 9001、ISO 9004等标准。ISO 9000旨在帮助组织建立、实施、维护和改进质量管理体系，提高产品和服务的质量，增强客户满意度。

#### ISO9000的特点

- **通用标准**：ISO 9000适用于各种行业和领域
- **质量管理体系**：ISO 9000是一个质量管理体系标准
- **客户导向**：ISO 9000强调客户导向和持续改进
- **过程方法**：ISO 9000采用过程方法来管理质量
- **国际标准**：ISO 9000是国际通用的质量管理体系标准

#### ISO9000的核心思想

ISO 9000的核心思想是通过建立、实施、维护和改进质量管理体系，提高产品和服务的质量，增强客户满意度。ISO 9000强调客户导向、过程方法、持续改进和基于事实的决策。

## CMM与ISO9000的历史背景对比

### 1. CMM的历史背景

#### 发展历程

- **1987年**：SEI发布了第一个CMM版本
- **1991年**：SEI发布了CMM 1.0版本
- **1993年**：SEI发布了CMM 1.1版本
- **2000年**：SEI发布了CMMI，将CMM与其他能力成熟度模型集成

#### 发展背景

CMM的起源可以追溯到20世纪80年代。当时，美国国防部（DoD）发现软件项目的失败率很高，主要原因是软件开发过程不成熟。为了解决这个问题，美国国防部委托卡内基梅隆大学软件工程研究所（SEI）开发一种评估和改进软件开发过程的方法。

#### 发展动机

CMM的发展动机是为了解决软件开发过程中的质量问题：

- **软件项目失败率高**：软件项目的失败率很高
- **软件开发过程不成熟**：软件开发过程不成熟
- **软件质量难以保证**：软件质量难以保证
- **软件生产效率低**：软件生产效率低

### 2. ISO9000的历史背景

#### 发展历程

- **1987年**：ISO发布了ISO 9000系列标准的第一版
- **1994年**：ISO发布了ISO 9000系列标准的第二版
- **2000年**：ISO发布了ISO 9000系列标准的第三版
- **2008年**：ISO发布了ISO 9000系列标准的第四版
- **2015年**：ISO发布了ISO 9000系列标准的第五版

#### 发展背景

ISO 9000的起源可以追溯到20世纪70年代。当时，国际贸易快速发展，各国对质量管理体系的要求不一致，导致贸易壁垒。为了解决这个问题，国际标准化组织（ISO）开始制定统一的质量管理体系标准。

#### 发展动机

ISO 9000的发展动机是为了解决国际贸易中的质量问题：

- **贸易壁垒**：各国对质量管理体系的要求不一致，导致贸易壁垒
- **质量标准不统一**：质量标准不统一，影响国际贸易
- **客户需求**：客户对产品和服务的质量要求越来越高
- **竞争压力**：企业面临越来越大的竞争压力

## CMM与ISO9000的应用领域对比

### 1. CMM的应用领域

CMM主要应用于软件开发领域：

- **软件开发**：CMM主要用于软件开发过程改进
- **软件工程**：CMM主要应用于软件工程领域
- **软件项目**：CMM主要应用于软件项目管理

#### CMM的应用示例

```java
// CMM的应用示例

class SoftwareProject {
    private RequirementManagement requirementManagement;
    private ProjectPlanning projectPlanning;
    private ProjectTracking projectTracking;
    private QualityAssurance qualityAssurance;
    private ConfigurationManagement configurationManagement;
    
    public SoftwareProject() {
        this.requirementManagement = new RequirementManagement();
        this.projectPlanning = new ProjectPlanning();
        this.projectTracking = new ProjectTracking();
        this.qualityAssurance = new QualityAssurance();
        this.configurationManagement = new ConfigurationManagement();
    }
    
    public void manage() {
        requirementManagement.manage();
        projectPlanning.plan();
        projectTracking.track();
        qualityAssurance.assure();
        configurationManagement.manage();
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

class ProjectTracking {
    public void track() {
        System.out.println("跟踪项目进度");
    }
}

class QualityAssurance {
    public void assure() {
        System.out.println("质量保证");
    }
}

class ConfigurationManagement {
    public void manage() {
        System.out.println("配置管理");
    }
}
```

### 2. ISO9000的应用领域

ISO 9000适用于各种行业和领域：

- **制造业**：ISO 9000广泛应用于制造业
- **服务业**：ISO 9000广泛应用于服务业
- **软件开发**：ISO 9000可以应用于软件开发
- **其他行业**：ISO 9000可以应用于其他行业

#### ISO9000的应用示例

```java
// ISO9000的应用示例

class Organization {
    private QualityManagementSystem qualityManagementSystem;
    private CustomerFocus customerFocus;
    private ProcessApproach processApproach;
    private ContinuousImprovement continuousImprovement;
    private FactBasedDecision factBasedDecision;
    
    public Organization() {
        this.qualityManagementSystem = new QualityManagementSystem();
        this.customerFocus = new CustomerFocus();
        this.processApproach = new ProcessApproach();
        this.continuousImprovement = new ContinuousImprovement();
        this.factBasedDecision = new FactBasedDecision();
    }
    
    public void manage() {
        qualityManagementSystem.establish();
        customerFocus.focus();
        processApproach.apply();
        continuousImprovement.improve();
        factBasedDecision.decide();
    }
}

class QualityManagementSystem {
    public void establish() {
        System.out.println("建立质量管理体系");
    }
}

class CustomerFocus {
    public void focus() {
        System.out.println("关注客户需求");
    }
}

class ProcessApproach {
    public void apply() {
        System.out.println("应用过程方法");
    }
}

class ContinuousImprovement {
    public void improve() {
        System.out.println("持续改进");
    }
}

class FactBasedDecision {
    public void decide() {
        System.out.println("基于事实的决策");
    }
}
```

## CMM与ISO9000的模型结构对比

### 1. CMM的模型结构

#### 五级成熟度模型

CMM定义了五个成熟度级别：

1. **初始级（Level 1: Initial）**：软件开发过程是混乱的，缺乏一致性和可预测性
2. **可重复级（Level 2: Repeatable）**：组织已经建立了基本的项目管理过程，可以重复以前成功项目的实践
3. **已定义级（Level 3: Defined）**：组织已经建立了标准的软件开发过程，所有项目都使用这个标准过程
4. **已管理级（Level 4: Managed）**：组织已经建立了度量和分析机制，可以定量地管理和控制软件开发过程
5. **优化级（Level 5: Optimizing）**：组织已经建立了持续改进的机制，可以持续改进软件开发过程

#### 18个关键过程域

CMM定义了18个关键过程域（Key Process Areas，KPAs）：

- **可重复级（6个）**：需求管理、项目计划、项目跟踪、子合同管理、质量保证、配置管理
- **已定义级（7个）**：组织过程焦点、组织过程定义、培训计划、集成软件管理、软件产品工程、组间协调、同行评审
- **已管理级（2个）**：定量过程管理、软件质量管理
- **优化级（3个）**：缺陷预防、技术革新、过程变更管理

### 2. ISO9000的模型结构

#### 质量管理体系原则

ISO 9000基于八个质量管理原则：

1. **客户导向**：组织依存于其客户，因此应理解客户当前和未来的需求，满足客户要求，并争取超越客户期望
2. **领导作用**：领导者确立组织统一的宗旨和方向，他们应该创造并保持使员工能充分参与实现组织目标的内部环境
3. **全员参与**：各级人员都是组织之本，只有他们的充分参与，才能使他们的才干为组织带来收益
4. **过程方法**：将活动和相关的资源作为过程进行管理，可以更高效地得到期望的结果
5. **系统方法**：将相互关联的过程作为系统来看待、理解和管理，有助于组织提高实现目标的有效性和效率
6. **持续改进**：持续改进总体业绩应当是组织的一个永恒目标
7. **基于事实的决策方法**：有效决策是建立在数据和信息分析的基础上
8. **互利的供方关系**：组织与供方是相互依存的，互利的关系可增强双方创造价值的能力

#### 质量管理体系要求

ISO 9001（ISO 9000系列标准的核心）规定了质量管理体系的要求：

1. **范围**：标准的适用范围
2. **引用标准**：引用的相关标准
3. **术语和定义**：相关的术语和定义
4. **质量管理体系**：质量管理体系的总要求
5. **管理职责**：管理者的职责
6. **资源管理**：资源的管理
7. **产品实现**：产品的实现
8. **测量、分析和改进**：测量、分析和改进

## CMM与ISO9000的评估方法对比

### 1. CMM的评估方法

CMM提供了两种评估方法：

- **CBA-IPI（CMM-Based Appraisal for Internal Process Improvement）**：主要用于组织内部的自我评估
- **SCAMPI（Standard CMMI Appraisal Method for Process Improvement）**：既可以用于组织内部的自我评估，也可以用于外部评估

#### CBA-IPI的特点

- **内部评估**：主要用于组织内部的自我评估
- **过程改进**：主要用于过程改进
- **灵活性高**：评估方法灵活，可以根据组织的需求调整

#### SCAMPI的特点

- **标准评估**：是CMMI的标准评估方法
- **内外评估**：既可以用于内部评估，也可以用于外部评估
- **权威性高**：评估结果具有权威性

### 2. ISO9000的评估方法

ISO 9000的评估方法主要是认证审核：

- **内部审核**：组织内部的自我审核
- **外部审核**：第三方认证机构的审核

#### 内部审核的特点

- **自我审核**：组织内部的自我审核
- **持续改进**：主要用于持续改进
- **灵活性高**：审核方法灵活，可以根据组织的需求调整

#### 外部审核的特点

- **第三方审核**：第三方认证机构的审核
- **认证审核**：主要用于认证
- **权威性高**：审核结果具有权威性

## CMM与ISO9000的优缺点对比

### 1. CMM的优缺点

#### 优点

- **过程改进**：CMM提供了系统化的过程改进方法
- **质量保证**：CMM提高了软件质量保证的水平
- **项目管理**：CMM改进了软件项目管理的方法
- **可预测性**：CMM提高了项目结果的可预测性
- **国际标准**：CMM成为国际软件过程改进的标准

#### 缺点

- **成本高**：实施CMM需要投入大量的人力、物力和财力
- **时间长**：实施CMM需要较长的时间
- **灵活性低**：CMM的过程较为僵化，灵活性较低
- **文档繁重**：CMM需要大量的文档，文档负担较重
- **文化冲突**：CMM可能与组织文化产生冲突

### 2. ISO9000的优缺点

#### 优点

- **通用性**：ISO 9000适用于各种行业和领域
- **国际标准**：ISO 9000是国际通用的质量管理体系标准
- **客户导向**：ISO 9000强调客户导向和持续改进
- **过程方法**：ISO 9000采用过程方法来管理质量
- **认证价值**：ISO 9000认证可以提高组织的信誉和竞争力

#### 缺点

- **成本高**：实施ISO 9000需要投入大量的人力、物力和财力
- **时间长**：实施ISO 9000需要较长的时间
- **灵活性低**：ISO 9000的过程较为僵化，灵活性较低
- **文档繁重**：ISO 9000需要大量的文档，文档负担较重
- **形式主义**：ISO 9000可能流于形式，失去实际意义

## CMM与ISO9000的主要区别

### 1. 定义和目标

| 对比维度 | CMM | ISO9000 |
|---------|-----|---------|
| 定义 | 能力成熟度模型 | 质量管理体系标准 |
| 目标 | 改进软件开发过程 | 建立质量管理体系 |
| 发布机构 | 卡内基梅隆大学软件工程研究所（SEI） | 国际标准化组织（ISO） |
| 发布时间 | 1987年 | 1987年 |

### 2. 应用领域

| 对比维度 | CMM | ISO9000 |
|---------|-----|---------|
| 应用领域 | 主要应用于软件开发 | 适用于各种行业和领域 |
| 专注领域 | 专注于软件开发过程 | 专注于质量管理体系 |
| 行业适用性 | 主要适用于软件行业 | 适用于各种行业 |

### 3. 模型结构

| 对比维度 | CMM | ISO9000 |
|---------|-----|---------|
| 模型结构 | 五级成熟度模型 | 质量管理体系原则和要求 |
| 过程域 | 18个关键过程域 | 八个质量管理原则 |
| 成熟度级别 | 五个成熟度级别 | 没有成熟度级别 |

### 4. 评估方法

| 对比维度 | CMM | ISO9000 |
|---------|-----|---------|
| 评估方法 | CBA-IPI、SCAMPI | 内部审核、外部审核 |
| 评估重点 | 评估软件开发过程 | 评估质量管理体系 |
| 评估结果 | 成熟度级别 | 认证结果 |

### 5. 优缺点

| 对比维度 | CMM | ISO9000 |
|---------|-----|---------|
| 优点 | 过程改进、质量保证、项目管理、可预测性 | 通用性、国际标准、客户导向、过程方法 |
| 缺点 | 成本高、时间长、灵活性低、文档繁重 | 成本高、时间长、灵活性低、文档繁重 |

## CMM与ISO9000的结合使用

### 1. 结合使用的优势

CMM和ISO 9000可以结合使用，发挥各自的优势：

- **互补性**：CMM和ISO 9000具有互补性，可以相互补充
- **全面性**：结合使用可以提供更全面的质量管理
- **有效性**：结合使用可以提高质量管理的有效性

### 2. 结合使用的方法

CMM和ISO 9000可以结合使用的方法：

- **以ISO 9000为基础**：以ISO 9000为基础建立质量管理体系
- **以CMM为补充**：以CMM为补充改进软件开发过程
- **持续改进**：通过持续改进来提高质量管理水平

#### 结合使用的示例

```java
// CMM与ISO9000结合使用的示例

class Organization {
    private QualityManagementSystem qualityManagementSystem;
    private SoftwareProcessImprovement softwareProcessImprovement;
    
    public Organization() {
        this.qualityManagementSystem = new QualityManagementSystem();
        this.softwareProcessImprovement = new SoftwareProcessImprovement();
    }
    
    public void manage() {
        qualityManagementSystem.establish();
        softwareProcessImprovement.improve();
    }
}

class QualityManagementSystem {
    public void establish() {
        System.out.println("建立ISO 9000质量管理体系");
    }
}

class SoftwareProcessImprovement {
    public void improve() {
        System.out.println("改进CMM软件开发过程");
    }
}
```

## CMM与ISO9000的选择建议

### 1. 选择CMM的情况

- **专注于软件开发**：如果组织专注于软件开发，可以选择CMM
- **软件过程改进**：如果组织需要改进软件开发过程，可以选择CMM
- **软件项目管理**：如果组织需要改进软件项目管理，可以选择CMM
- **软件质量保证**：如果组织需要提高软件质量保证水平，可以选择CMM

### 2. 选择ISO9000的情况

- **多行业应用**：如果组织需要在多个行业应用，可以选择ISO 9000
- **质量管理体系**：如果组织需要建立质量管理体系，可以选择ISO 9000
- **客户要求**：如果客户要求组织通过ISO 9000认证，可以选择ISO 9000
- **国际贸易**：如果组织需要参与国际贸易，可以选择ISO 9000

### 3. 结合使用的情况

- **全面质量管理**：如果组织需要全面质量管理，可以结合使用CMM和ISO 9000
- **软件质量保证**：如果组织需要提高软件质量保证水平，可以结合使用CMM和ISO 9000
- **国际标准**：如果组织需要与国际标准接轨，可以结合使用CMM和ISO 9000

## 总结

CMM（Capability Maturity Model，能力成熟度模型）和ISO 9000是两个不同的质量管理体系。CMM是由美国卡内基梅隆大学软件工程研究所（SEI）开发的软件过程改进模型，专注于软件开发过程。ISO 9000是由国际标准化组织（ISO）制定的质量管理体系标准，适用于各种行业和领域。CMM和ISO 9000在定义、历史背景、应用领域、模型结构、评估方法、优缺点等方面都有显著的区别。CMM主要应用于软件开发领域，是一个五级的成熟度模型，定义了18个关键过程域。ISO 9000适用于各种行业和领域，基于八个质量管理原则，规定了质量管理体系的要求。CMM和ISO 9000可以结合使用，发挥各自的优势，提供更全面的质量管理。选择CMM还是ISO 9000，需要根据组织的实际情况和需求来决定。如果组织专注于软件开发、需要改进软件开发过程、需要改进软件项目管理、需要提高软件质量保证水平，可以选择CMM；如果组织需要在多个行业应用、需要建立质量管理体系、客户要求组织通过ISO 9000认证、需要参与国际贸易，可以选择ISO 9000；如果组织需要全面质量管理、需要提高软件质量保证水平、需要与国际标准接轨，可以结合使用CMM和ISO 9000。