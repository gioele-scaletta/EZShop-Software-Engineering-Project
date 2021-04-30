# Project Estimation

Authors: Jose Antonio Antona Diaz, Giuseppe D'Andrea, Marco Riggio, Gioele Scaletta

Date: 30/04/2021

Version: 1.0

# Contents

- [Estimate by product decomposition](#estimate-by-product-decomposition)
- [Estimate by activity decomposition](#estimate-by-activity-decomposition)

# Estimation approach

Consider the EZShop project as described in YOUR requirement document, assume that you are going to develop the project INDEPENDENT of the deadlines of the course

# Estimate by product decomposition

|             | Estimate                        |
| ----------- | ------------------------------- |
| NC =  Estimated number of classes to be developed | 20 |
| A = Estimated average size per class, in LOC | 150 |
| S = Estimated size of project, in LOC (= NC * A) | 3000 |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour) | 300 ph |
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro) | 9000 |
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week ) | 1,875 weeks |

# Estimate by activity decomposition

|         Activity name    | Estimated effort (person hours)   |
| ----------- | ------------------------------- |
| Requirement Document | **55** |
| &ensp; &ensp; Concept development | *30* |
| &ensp; &ensp; Document drafting | *15* |
| &ensp; &ensp; Document V&V | *10* |
| GUI | **65** |
| &ensp; &ensp; Usability study | *20* |
| &ensp; &ensp; Development | *35* |
| &ensp; &ensp; GUI V&V | *10* |
| Design document | **50** |
| &ensp; &ensp; High level | *10* |
| &ensp; &ensp; Low level | *30* |
| &ensp; &ensp; Design V&V | *10* |
| Coding | **85** |
| &ensp; &ensp; Java Classes | *70* |
| &ensp; &ensp; DB layer | *15* |
| Testing | **30** |
| Integration | **15** |

<br>
<br>

```plantuml
ganttscale daily
scale 2

saturday are closed
sunday are closed
Project starts the 5th of april 2021
[Requirement Document] as [TASK1] lasts 2 days
[TASK1] is colored in Red

[Concept development] lasts 1 day
[Document drafting] lasts 1 day
[Document V&V] lasts 1 day

[Concept development]->[Document drafting] 
[Concept development]->[Document V&V] 

[GUI]  starts the 6th of april 2021 and lasts 3 days and is colored in Red

[Usability study] lasts 1 day and starts the 6th of april 2021
[Development] lasts 2 days and starts the 7th of april 2021
[GUI V&V] lasts 1 day and starts the 8th of april 2021

[Usability study]->[Development] 
[Usability study]->[GUI V&V] 

[Design document]  starts the 8th of april 2021 and lasts 2 days and is colored in Red

[High level] lasts 1 day and starts the 8th of april 2021
[Low level] lasts 2 days and starts the 8th of april 2021
[Design V&V] lasts 1 day and starts the 9th of april 2021

[High level]->[Design V&V]

[Coding]  starts the 12th of april 2021 and lasts 3 days and is colored in Red

[Java Classes] lasts 3 day and starts the 12th of april 2021
[DB layer] lasts 2 days and starts the 13th of april 2021

[Testing]  starts the 15th of april 2021 and lasts 2 days and is colored in Red

[Integration] starts the 16th of april 2021 and lasts 1 day and is colored in Red 
```
