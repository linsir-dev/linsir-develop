# CMMI与CMM的区别？

## 概述

CMMI（Capability Maturity Model Integration，能力成熟度模型集成）是CMM（Capability Maturity Model，能力成熟度模型）的继承和发展。CMMI在CMM的基础上，集成了多个能力成熟度模型，扩展了应用领域，提高了灵活性，成为当前软件过程改进的主流模型。

## CMMI与CMM的定义对比

### 1. CMM的定义

CMM（Capability Maturity Model，能力成熟度模型）是由美国卡内基梅隆大学软件工程研究所（SEI）在20世纪80年代末开发的一种软件过程改进模型。CMM旨在帮助软件开发组织改进其软件开发过程，提高软件开发的质量和生产效率。

#### CMM的特点

- **专注于软件**：CMM专注于软件开发过程
- **五级模型**：CMM是一个五级的成熟度模型
- **18个关键过程域**：CMM定义了18个关键过程域
- **软件工程**：CMM主要应用于软件工程领域

### 2. CMMI的定义

CMMI（Capability Maturity Model Integration，能力成熟度模型集成）是由美国卡内基梅隆大学软件工程研究所（SEI）在2000年发布的一种能力成熟度模型集成。CMMI集成了多个能力成熟度模型，包括软件工程（SW-CMM）、系统工程（SE-CMM）、集成产品开发（IPD-CMM）等，扩展了应用领域，提高了灵活性。

#### CMMI的特点

- **集成多个模型**：CMMI集成了多个能力成熟度模型
- **多种表示方法**：CMMI提供了多种表示方法（阶段式、连续式）
- **多个应用领域**：CMMI扩展了应用领域，不仅限于软件开发
- **更高的灵活性**：CMMI提高了灵活性，适应不同的组织和项目

## CMMI与CMM的历史背景对比

### 1. CMM的历史背景

#### 发展历程

- **1987年**：SEI发布了第一个CMM版本
- **1991年**：SEI发布了CMM 1.0版本
- **1993年**：SEI发布了CMM 1.1版本
- **2000年**：SEI发布了CMMI，将CMM与其他能力成熟度模型集成

#### 发展背景

CMM的起源可以追溯到20世纪80年代。当时，美国国防部（DoD）发现软件项目的失败率很高，主要原因是软件开发过程不成熟。为了解决这个问题，美国国防部委托卡内基梅隆大学软件工程研究所（SEI）开发一种评估和改进软件开发过程的方法。

### 2. CMMI的历史背景

#### 发展历程

- **2000年**：SEI发布了CMMI 1.0版本
- **2002年**：SEI发布了CMMI 1.1版本
- **2006年**：SEI发布了CMMI 1.2版本
- **2010年**：SEI发布了CMMI 1.3版本
- **2018年**：CMMI研究院发布了CMMI 2.0版本

#### 发展背景

CMMI的发展是为了解决CMM的局限性。CMM专注于软件开发过程，无法满足系统工程、集成产品开发等其他领域的需求。为了解决这个问题，SEI开发了CMMI，将多个能力成熟度模型集成，扩展了应用领域，提高了灵活性。

## CMMI与CMM的模型结构对比

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

### 2. CMMI的模型结构

#### 五级成熟度模型

CMMI定义了五个成熟度级别：

1. **初始级（Level 1: Initial）**：过程是混乱的，缺乏一致性和可预测性
2. **已管理级（Level 2: Managed）**：组织已经建立了基本的项目管理过程，可以重复以前成功项目的实践
3. **已定义级（Level 3: Defined）**：组织已经建立了标准的过程，所有项目都使用这个标准过程
4. **量化管理级（Level 4: Quantitatively Managed）**：组织已经建立了度量和分析机制，可以定量地管理和控制过程
5. **优化级（Level 5: Optimizing）**：组织已经建立了持续改进的机制，可以持续改进过程

#### 22个过程域

CMMI定义了22个过程域（Process Areas，PAs）：

- **已管理级（7个）**：需求管理、项目计划、项目监控、供应商协议管理、度量与分析、过程和产品质量保证、配置管理
- **已定义级（11个）**：需求开发、技术解决方案、产品集成、验证、确认、组织过程焦点、组织过程定义、组织培训、集成项目管理、风险管理、决策分析与解决
- **量化管理级（2个）**：组织过程性能、定量项目管理
- **优化级（2个）**：组织创新与部署、原因分析与解决

#### 两种表示方法

CMMI提供了两种表示方法：

- **阶段式表示法（Staged Representation）**：类似于CMM，将过程域按成熟度级别分组
- **连续式表示法（Continuous Representation）**：将过程域按过程能力级别分组，提供更大的灵活性

## CMMI与CMM的应用领域对比

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

### 2. CMMI的应用领域

CMMI扩展了应用领域，不仅限于软件开发：

- **软件开发**：CMMI可以应用于软件开发过程改进
- **系统工程**：CMMI可以应用于系统工程过程改进
- **集成产品开发**：CMMI可以应用于集成产品开发过程改进
- **服务管理**：CMMI可以应用于服务管理过程改进

#### CMMI的应用示例

```java
// CMMI的应用示例

class Project {
    private RequirementManagement requirementManagement;
    private ProjectPlanning projectPlanning;
    private ProjectMonitoring projectMonitoring;
    private SupplierAgreementManagement supplierAgreementManagement;
    private MeasurementAndAnalysis measurementAndAnalysis;
    private ProcessAndProductQualityAssurance processAndProductQualityAssurance;
    private ConfigurationManagement configurationManagement;
    
    public Project() {
        this.requirementManagement = new RequirementManagement();
        this.projectPlanning = new ProjectPlanning();
        this.projectMonitoring = new ProjectMonitoring();
        this.supplierAgreementManagement = new SupplierAgreementManagement();
        this.measurementAndAnalysis = new MeasurementAndAnalysis();
        this.processAndProductQualityAssurance = new ProcessAndProductQualityAssurance();
        this.configurationManagement = new ConfigurationManagement();
    }
    
    public void manage() {
        requirementManagement.manage();
        projectPlanning.plan();
        projectMonitoring.monitor();
        supplierAgreementManagement.manage();
        measurementAndAnalysis.analyze();
        processAndProductQualityAssurance.assure();
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

class ProjectMonitoring {
    public void monitor() {
        System.out.println("监控项目进度");
    }
}

class SupplierAgreementManagement {
    public void manage() {
        System.out.println("管理供应商协议");
    }
}

class MeasurementAndAnalysis {
    public void analyze() {
        System.out.println("度量与分析");
    }
}

class ProcessAndProductQualityAssurance {
    public void assure() {
        System.out.println("过程和产品质量保证");
    }
}

class ConfigurationManagement {
    public void manage() {
        System.out.println("配置管理");
    }
}
```

## CMMI与CMM的评估方法对比

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

### 2. CMMI的评估方法

CMMI提供了三种评估方法：

- **SCAMPI A类评估**：CMMI的最高级别评估，也是最严格的评估
- **SCAMPI B类评估**：CMMI的中等级别评估，比A类评估宽松一些
- **SCAMPI C类评估**：CMMI的最低级别评估，也是最灵活的评估

#### SCAMPI A类评估的特点

- **严格性**：评估过程严格，要求高
- **权威性**：评估结果具有权威性
- **全面性**：评估内容全面，覆盖所有过程域

#### SCAMPI B类评估的特点

- **灵活性**：评估过程灵活，可以根据组织的需求调整
- **实用性**：评估结果实用，可以直接用于过程改进
- **成本较低**：评估成本较低

#### SCAMPI C类评估的特点

- **灵活性高**：评估过程灵活，可以根据组织的需求调整
- **成本低**：评估成本低
- **快速评估**：评估速度快

## CMMI与CMM的优缺点对比

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

### 2. CMMI的优缺点

#### 优点

- **集成多个模型**：CMMI集成了多个能力成熟度模型
- **扩展应用领域**：CMMI扩展了应用领域，不仅限于软件开发
- **提高灵活性**：CMMI提高了灵活性，适应不同的组织和项目
- **多种表示方法**：CMMI提供了多种表示方法，满足不同的需求
- **更好的兼容性**：CMMI与ISO 9001等标准有更好的兼容性

#### 缺点

- **成本高**：实施CMMI需要投入大量的人力、物力和财力
- **时间长**：实施CMMI需要较长的时间
- **复杂度高**：CMMI的复杂度较高，实施难度较大
- **文档繁重**：CMMI需要大量的文档，文档负担较重
- **文化冲突**：CMMI可能与组织文化产生冲突

## CMMI与CMM的对比总结

### 1. 主要区别

| 对比维度 | CMM | CMMI |
|---------|-----|------|
| 定义 | 能力成熟度模型 | 能力成熟度模型集成 |
| 发布时间 | 1987年 | 2000年 |
| 应用领域 | 主要应用于软件开发 | 扩展到系统工程、集成产品开发、服务管理等多个领域 |
| 模型结构 | 五级成熟度模型，18个关键过程域 | 五级成熟度模型，22个过程域 |
| 表示方法 | 只有阶段式表示法 | 提供阶段式和连续式两种表示法 |
| 评估方法 | CBA-IPI、SCAMPI | SCAMPI A类、B类、C类评估 |
| 灵活性 | 灵活性较低 | 灵活性较高 |
| 兼容性 | 与ISO 9001等标准的兼容性较差 | 与ISO 9001等标准有更好的兼容性 |

### 2. 相同点

- **五级成熟度模型**：CMM和CMMI都是五级成熟度模型
- **过程改进**：CMM和CMMI都旨在帮助组织改进过程
- **质量保证**：CMM和CMMI都提高了质量保证的水平
- **项目管理**：CMM和CMMI都改进了项目管理的方法
- **可预测性**：CMM和CMMI都提高了项目结果的可预测性

### 3. 不同点

- **应用领域**：CMM主要应用于软件开发，CMMI扩展到多个领域
- **模型结构**：CMM有18个关键过程域，CMMI有22个过程域
- **表示方法**：CMM只有阶段式表示法，CMMI提供阶段式和连续式两种表示法
- **评估方法**：CMM的评估方法较少，CMMI的评估方法较多
- **灵活性**：CMM的灵活性较低，CMMI的灵活性较高
- **兼容性**：CMM与其他标准的兼容性较差，CMMI与其他标准的兼容性较好

## CMMI与CMM的选择建议

### 1. 选择CMM的情况

- **专注于软件开发**：如果组织专注于软件开发，可以选择CMM
- **预算有限**：如果组织预算有限，可以选择CMM
- **时间紧迫**：如果组织时间紧迫，可以选择CMM
- **简单项目**：如果组织处理简单的项目，可以选择CMM

### 2. 选择CMMI的情况

- **多领域应用**：如果组织需要在多个领域应用，可以选择CMMI
- **复杂项目**：如果组织处理复杂的项目，可以选择CMMI
- **长期发展**：如果组织有长期发展的计划，可以选择CMMI
- **国际标准**：如果组织需要与国际标准接轨，可以选择CMMI

## 总结

CMMI（Capability Maturity Model Integration，能力成熟度模型集成）是CMM（Capability Maturity Model，能力成熟度模型）的继承和发展。CMMI在CMM的基础上，集成了多个能力成熟度模型，扩展了应用领域，提高了灵活性，成为当前软件过程改进的主流模型。CMMI与CMM的主要区别包括：CMMI集成了多个能力成熟度模型，扩展了应用领域，提供了多种表示方法，提高了灵活性，与其他标准有更好的兼容性。CMMI与CMM的相同点包括：都是五级成熟度模型，都旨在帮助组织改进过程，都提高了质量保证的水平，都改进了项目管理的方法，都提高了项目结果的可预测性。CMMI与CMM的不同点包括：应用领域不同、模型结构不同、表示方法不同、评估方法不同、灵活性不同、兼容性不同。选择CMM还是CMMI，需要根据组织的实际情况和需求来决定。如果组织专注于软件开发、预算有限、时间紧迫、处理简单的项目，可以选择CMM；如果组织需要在多个领域应用、处理复杂的项目、有长期发展的计划、需要与国际标准接轨，可以选择CMMI。