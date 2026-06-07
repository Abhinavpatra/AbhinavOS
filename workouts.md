# ATHLETE PROFILE
  
```yaml
name: Badminton Athletic Development Block
duration_weeks: 7

athlete:
  age: 22
  height_cm: 178
  weight_kg: 79
  bodyfat_percent: 19

goals:
  - badminton_performance
  - acceleration
  - agility
  - repeat_sprint_ability
  - vertical_jump
  - maintain_strength
  - increase_back_width
  - increase_shoulder_size
  - improve_joint_resilience
  - reduce_bodyfat

constraints:
  - desk_job_9_to_5
  - gym_access
  - field_access
  - no_badminton_court
```

---

# TRAINING PHASES

```yaml
phase_1:
  weeks: [1,2]
  focus:
    - tissue_preparation
    - movement_quality
    - aerobic_reintroduction

phase_2:
  weeks: [3,4]
  focus:
    - volume_building
    - sprint_capacity
    - jump_capacity

phase_3:
  weeks: [5,6]
  focus:
    - maximum_intensity
    - speed
    - power

phase_4:
  weeks: [7]
  focus:
    - performance_peak
    - fatigue_reduction
```

---

# WEEK STRUCTURE

```yaml
sunday:
  type: lower_strength_jump

monday:
  type: speed_agility

tuesday:
  type: push

wednesday:
  type: conditioning

thursday:
  type: pull

friday:
  type: power_agility

saturday:
  type: recovery_stability
```

---

# DAILY ARMOR BLOCK

Every single day.

```yaml
daily_armor:

  ankle:
    - exercise: tibialis_raise
      sets: 2
      reps: 20

    - exercise: single_leg_calf_raise
      sets: 2
      reps: 20

  knee:
    - exercise: spanish_squat_hold
      sets: 2
      duration_sec: 45

  core:
    - exercise: pallof_press
      sets: 2
      reps: 15

    - exercise: side_plank
      sets: 2
      duration_sec: 30

  shoulder:
    - exercise: cable_external_rotation
      sets: 2
      reps: 20

    - exercise: pitchers_path
      sets: 2
      reps: 15

  neck:
    - flexion: 2x20
    - extension: 2x20
    - lateral_flexion: 2x15
```

---

# SUNDAY — LOWER STRENGTH + JUMP

```yaml
session: lower_strength_jump

warmup:
  - ankle_hops
  - leg_swings
  - bodyweight_squats
  - walking_lunges

main:

  back_squat:
    sets: 5
    reps: 3
    intensity: 85_to_90_percent

  romanian_deadlift:
    sets: 4
    reps: 6

  weighted_box_jump:
    sets: 5
    reps: 3

  split_rebound_double_switch:
    sets: 4
    reps_each_side: 5

  standing_calf_raise:
    sets: 4
    reps: 12

  tibialis_raise:
    sets: 4
    reps: 20

  weighted_plank:
    sets: 3
    duration_sec: 45
```

---

# MONDAY — SPEED + AGILITY

```yaml
session: speed_agility

warmup:
  - a_skips
  - b_skips
  - high_knees
  - ankle_hops
  - leg_swings

acceleration:

  week_1_2:
    6x20m

  week_3_4:
    8x20m

  week_5_7:
    10x20m

rest:
  full_recovery

speed:

  sprint:
    sets: 4
    distance: 40m

agility:

  shuttle_5_10_5:
    rounds: 6

  lateral_reaction_drill:
    duration_min: 10

  badminton_shadow:
    duration_min: 10
```

---

# TUESDAY — PUSH

Uses many exercises already present in your push-day notes. 

```yaml
session: push

weighted_dips:
  sets: 4
  reps: 5

incline_bench:
  sets: 4
  reps: 6

push_press:
  sets: 5
  reps: 3

dumbbell_shoulder_press:
  sets: 3
  reps: 10

lu_raise:
  sets: 3
  reps: 15

lateral_raise:
  sets: 4
  reps: 15

face_pull:
  sets: 4
  reps: 20

bottoms_up_hold:
  sets: 3
  duration_sec: 30
```

---

# WEDNESDAY — CONDITIONING

Based on the interval structure already present in your notes. 

```yaml
session: conditioning

week_1:
  intervals: 8

week_2:
  intervals: 10

week_3:
  intervals: 12

week_4:
  intervals: 12

week_5:
  intervals: 10
  hard_sec: 40
  easy_sec: 80

week_6:
  intervals: 12
  hard_sec: 40
  easy_sec: 80

week_7:
  intervals: 14
  hard_sec: 40
  easy_sec: 80

modality:
  - running
  - treadmill
  - bike
```

---

# THURSDAY — PULL

Built from your pull-day exercise list. 

```yaml
session: pull

weighted_pullup:
  sets: 5
  reps: 3

barbell_row:
  sets: 4
  reps: 6

lat_pulldown:
  sets: 3
  reps: 10

gironda_pullup:
  sets: 3
  reps_to_failure_minus_2: true

reeves_shrug:
  sets: 3
  reps: 12

stahl_rotation:
  sets: 3
  reps: 15

hammer_curl:
  sets: 3
  reps: 12

plate_pinch_hold:
  sets: 3
  duration_sec: 45
```

---

# FRIDAY — POWER + AGILITY

```yaml
session: power_day

push_press:
  sets: 5
  reps: 3
  intent: explosive

broad_jump:
  sets: 6
  reps: 2

lateral_bounds:
  sets: 5
  reps_each_side: 5

single_leg_landing:
  sets: 3
  reps_each_leg: 8

pogo_jump:
  sets: 3
  reps: 20

single_leg_pogo:
  sets: 3
  reps_each_leg: 10

pallof_press:
  sets: 3
  reps: 15

hanging_leg_raise:
  sets: 3
  reps: 12
```

---

# SATURDAY — RECOVERY + STABILITY

Built from the shoulder-health, spinal, and core work you've collected.  

```yaml
session: recovery

zone_2:
  duration_min: 30_to_45

y_raise:
  3x15

external_rotation:
  3x20

pitchers_path:
  3x15

wall_raise:
  3x15

face_pull:
  3x20

overhead_hold:
  3x30sec

jefferson_curl:
  3x10

reverse_hyper:
  3x15

barbell_side_bend:
  3x15
```

---

# PERFORMANCE TRACKING

```yaml
daily:

  bodyweight:
    unit: kg

  sleep:
    unit: hours

  calories:
    unit: kcal

  protein:
    unit: grams

  water:
    unit: liters

weekly:

  pullups_max

  weighted_pullup_1rm_est

  squat_3rm

  vertical_jump_cm

  broad_jump_cm

  sprint_20m_sec

  resting_heart_rate

  morning_energy:
    scale: 1_to_10

  soreness:
    scale: 1_to_10
```

---

# AUTO-REGULATION RULES

This is the most important section for the app.

```yaml
if:
  sleep < 6h
then:
  reduce_volume: 20_percent

if:
  soreness > 8
then:
  remove_plyometrics

if:
  resting_heart_rate:
    increase: >10_percent
then:
  convert_hard_session_to_zone2

if:
  bodyweight_loss:
    >1_percent_per_week
then:
  increase_calories: 300

if:
  sprint_time:
    worsening_2_weeks
then:
  reduce_conditioning_volume
```

---

# SUCCESS CRITERIA AFTER 7 WEEKS

```yaml
target:

  weight:
    75_to_76kg

  bodyfat:
    14_to_16_percent

  pullups:
    22_to_25

  weighted_pullup:
    25kg_plus

  squat:
    maintain_or_improve

  vertical_jump:
    plus_5cm

  sprint_20m:
    significantly_faster

  resting_hr:
    lower_than_start

  subjective:
    feels_explosive_on_court_movements
```

