# ATHLETEOS

## Personal Athletic Operating System

### Version 1.0

### Built Specifically For: Abhinav

---

# PROJECT PURPOSE

AthleteOS is not a fitness app.

It is not a workout generator.

It is not a coaching platform.

It is not a social network.

It is not MyFitnessPal.

It is not Strong.

It is not Hevy.

It is not Strava.

AthleteOS exists for one purpose:

> To act as a personal operating system for Abhinav's athletic development over the next several years.

The app should remove all friction between:

* Seeing today's training
* Executing today's training
* Logging today's training
* Measuring progress

The app should feel closer to a pilot's cockpit or an athlete's training journal than a fitness application.

---

# USER PROFILE

User: Abhinav

Age: 22

Height: 5'10"

Weight: 79kg

Body Fat: ~19%

Current Strength:

Pullups:
20 reps

Weighted Pullup:
17.5kg x 1

Back Squat:
120kg x 5

Standing OHP:
60kg x 8

Goals:

Primary:

* Become an elite-level badminton athlete again

Secondary:

* Improve speed
* Improve agility
* Improve conditioning
* Improve jump height
* Improve movement quality
* Improve repeat sprint ability

Tertiary:

* Maintain and gradually improve strength
* Grow shoulders
* Grow back
* Maintain chest
* Improve forearms
* Improve lower back

---

# DEVELOPMENT PHILOSOPHY

There is exactly ONE user.

Therefore:

NO authentication.

NO account system.

NO cloud sync.

NO social features.

NO subscriptions.

NO ads.

NO workout marketplace.

NO AI workout generation.

NO dynamic planning.

The training plan already exists.

The app executes it.

---

# TECHNOLOGY STACK

Android Only

Language:
Kotlin

UI:
Jetpack Compose

Architecture:
MVVM + Clean Architecture

Dependency Injection:
Hilt

Navigation:
Navigation Compose

Database:
Room

Preferences:
DataStore

Notifications:
WorkManager

Charts:
Vico Charts

Concurrency:
Coroutines + Flow

Build:
Gradle Kotlin DSL

Target:
Android 10+

Why?

Because this stack is:

* Fast
* Offline
* Native
* Stable
* Future-proof
* Perfect for a personal-use application

---

# APP STARTUP

Launch

↓

Database exists?

NO

↓

Seed Database

↓

Load Program

↓

Home Screen

YES

↓

Home Screen

No onboarding.

No setup.

No account creation.

---

# DATABASE SEEDING

On first launch:

Load bundled JSON assets.

These contain:

Week 1

Week 2

Week 3

Week 4

Week 5

Week 6

Week 7

All workouts.

All exercises.

All target weights.

All target reps.

All target rest periods.

---

# HOME SCREEN

Purpose:

Answer:

"What am I doing today?"

Layout:

Header

TODAY

Monday

Speed + Agility

Week 1

Day 1

Progress:
0%

Estimated Time:
60 min

Button:

START WORKOUT

---

Below:

Tomorrow

Push

75 min

---

Below:

Current Week

Visual Week Timeline

Mon Tue Wed Thu Fri Sat Sun

Current day highlighted.

---

# WEEKS SCREEN

Scrollable vertical list.

Week 1

Week 2

Week 3

Week 4

Week 5

Week 6

Week 7

Current week:

Highlighted.

Completed weeks:

Green.

Future weeks:

Gray.

---

# WEEK DETAILS SCREEN

Week 3

Progress:
68%

Cards:

Monday
Speed + Agility

Tuesday
Push

Wednesday
Conditioning

Thursday
Pull

Friday
Power + Agility

Saturday
Recovery + Stability

Sunday
Lower Strength + Jump

Each card shows:

Completion %

Duration

Status

---

# DAY SCREEN

Example:

Monday

Speed + Agility

Progress:
0%

Estimated:
60 min

---

Exercise List

□ Warmup

□ Accelerations

□ Sprints

□ Shuttle Runs

□ Shadow Footwork

□ Cooldown

---

Large Progress Bar

Top of screen.

---

# WORKOUT EXECUTION

Exercise Card

Weighted Pullup

Target:
15kg

5 x 3

Rest:
180 sec

---

Expandable.

Shows:

Set 1

Target:
15 x 3

Actual Weight:
[15]

Actual Reps:
[3]

✓

---

Set 2

Target:
15 x 3

Actual Weight:
[ ]

Actual Reps:
[ ]

□

---

# PLANNED VS ACTUAL

Every set stores:

Target Weight

Target Reps

Actual Weight

Actual Reps

Completion Status

This is critical.

Some days:

Target:
105kg

Actual:
100kg

Still counts.

Workout complete.

---

# COMPLETION SYSTEM

Single Tap

Complete Set

Double Tap

Complete Exercise

Long Press

Undo

---

# REST TIMER

Floating Timer

Top Right

Presets:

30

60

90

120

180

Custom

---

After set completion:

Start Rest?

YES

Automatically starts countdown.

---

# TRAINING PROGRAM

PROGRAM NAME

Badminton Athleticism Block

Duration:
7 Weeks

---

MONDAY

Speed + Agility

Warmup

Accelerations
6x20m

Sprints
4x40m

5-10-5 Shuttle
6 Rounds

Shadow Footwork
10 min

Cooldown

---

TUESDAY

Push

Weighted Dips
4x5

Incline Bench
4x6

Push Press
5x3

DB Shoulder Press
3x10

Lu Raise
3x15

Lateral Raise
4x15

Face Pull
4x20

Bottoms-Up Hold
3 Sets

---

WEDNESDAY

Conditioning

30s Hard

90s Easy

Week 1:
8

Week 2:
10

Week 3:
12

Week 4:
12

Week 5:
10 (40/80)

Week 6:
12

Week 7:
14

---

THURSDAY

Pull

Weighted Pullup
5x3

Barbell Row
4x6

Lat Pulldown
3x10

Gironda Pullup
3 Sets

Reeves Shrug
3x12

Stahl Rotation
3x15

Hammer Curl
3x12

Plate Pinch Hold
3 Sets

---

FRIDAY

Power + Agility

Push Press
5x3

Broad Jump
6x2

Lateral Bounds
5x5

Single Leg Landing
3x8

Pogo Jumps
3x20

Single Leg Pogos
3x10

Pallof Press
3x15

Hanging Leg Raise
3x12

---

SATURDAY

Recovery + Stability

Zone 2

30-45 min

Y Raise
3x15

External Rotation
3x20

Pitcher's Path
3x15

Wall Raise
3x15

Face Pull
3x20

Overhead Hold
3x30 sec

Jefferson Curl
3x10

Reverse Hyper
3x15

Side Bend
3x15

---

SUNDAY

Lower Strength + Jump

Back Squat
5x3

Romanian Deadlift
4x6

Box Jump
5x3

Split Rebound Double Switch
4x5

Standing Calf Raise
4x12

Tibialis Raise
4x20

Weighted Plank
3x45s

---

# DAILY ARMOR BLOCK

Separate screen.

Daily checklist.

Tibialis Raise

Single Leg Calf Raise

Spanish Squat Hold

Pallof Press

Side Plank

External Rotation

Pitcher's Path

Neck Flexion

Neck Extension

Side Neck

Track completion streak.

---

# METRICS SCREEN

Most important screen after workouts.

Sections:

BODY

Weight

Body Fat

Waist

---

RECOVERY

Sleep

Resting Heart Rate

Water

---

STRENGTH

Pullups

Weighted Pullups

Squat

Push Press

---

ATHLETICISM

Vertical Jump

Broad Jump

20m Sprint

---

Charts:

7d

30d

90d

All Time

---

# PR SYSTEM

Automatic.

New Squat PR

New Pullup PR

New Sprint PR

New Jump PR

New Push Press PR

Generated automatically.

---

# HISTORY SCREEN

Calendar

Each day:

Completed

Partial

Missed

---

Tap Day

View entire workout.

Every set.

Every weight.

Every rep.

---

# NOTES

Workout Notes

Example:

"Left calf tight."

"Shoulder felt amazing."

"Push Press easy."

Attached to workout day.

---

# SETTINGS

Dark Mode

Notifications

Rest Timer Defaults

Metric Units

Export Data

Import Data

---

# NOTIFICATIONS

6:00 PM

Today's Training:

Speed + Agility

---

9:00 PM

Workout Not Started

---

No spam.

---

# DASHBOARD SCORE

Weekly calculated score.

Strength

Power

Conditioning

Recovery

Athleticism

Overall

Used only for trend visibility.

---

# VISUAL DESIGN

Dark-first.

Colors:

Background:
Near Black

Cards:
Dark Gray

Primary:
Electric Blue

Success:
Green

Warning:
Amber

Failure:
Red

---

Typography:

Material 3

Large numbers.

Large touch targets.

Everything readable during workouts.

---

# PACKAGE STRUCTURE

app

core

data

database

domain

features

home

weeks

workout

metrics

history

settings

notifications

navigation

---

# VERSION 1 SUCCESS

When Abhinav opens the app:

1. He immediately sees today's training.
2. He never wonders what to do next.
3. Every set is logged in under 2 seconds.
4. Every workout is stored forever.
5. Progress is visible.
6. The app feels instant.
7. The app works entirely offline.
8. The app becomes the single source of truth for all training.

This is not a fitness app.

It is a personal athletic operating system.