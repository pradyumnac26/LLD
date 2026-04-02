# Splitwise — LLD

---

## Requirements

- Users can be created and added to groups.
- A user can add an expense to a group — specifying who paid, total amount, and split type.
- Four split types supported:
    - **Equal** — divide equally among all participants
    - **Exact** — each person owes a specific amount
    - **Percentage** — each person owes a percentage of the total
    - **Share-based** — each person gets a number of shares; amount proportional to shares
- System tracks balances between users — who owes whom how much.
- A user can view their total balance (net amount owed or receivable).
- A user can settle up with another user — marks the debt as paid.
- View all expenses in a group.

**Out of scope:**
- Payment processing
- Currency conversion
- Expense receipts / photos
- Notifications
- Debt simplification (minimize transactions)

---

## Entities

| Entity | Role |
|---|---|
| `SplitwiseService` | Singleton orchestrator — manages users, groups, expenses, settlements |
| `models.User` | Has name and email. Identified by id. |
| `Group` | Has members and a list of expenses |
| `Expense` | Holds payer, total amount, description, and list of Splits |
| `Split` | Value object — user + amount they owe for this expense |
| `SplitStrategy` | Interface — calculates how an expense is divided |

---

## Relationships

```
SplitwiseService
  └── Map<userId, models.User>
  └── Map<groupId, Group>
  └── Map<"payerId:owerId", Double>   // balance map

Group
  └── List<models.User>     members
  └── List<Expense>  expenses

Expense
  └── models.User payer
  └── double totalAmount
  └── List<Split> splits

Split
  └── models.User user
  └── double amount    // what this user owes
```

---

## Class Design

### Class: models.User
```java
class models.User:
  - id    : String  (final, UUID)
  - name  : String  (final)
  - email : String  (final)

  + getId()    -> String
  + getName()  -> String
  + getEmail() -> String
```

### Class: Split (value object)

```java
import models.User;

class Split:
        -user   :

User(final)
  -amount :

double(final)   // what this user owes

  +

getUser()   ->User
  +

getAmount() ->double
```

### Class: Expense
```java
class Expense:
  - id          : String        (final, UUID)
  - description : String        (final)
  - payer       : models.User          (final)
  - totalAmount : double        (final)
  - splits      : List<Split>   (final)

  + getId()
  + getDescription()
  + getPayer()
  + getTotalAmount()
  + getSplits()
```

### Interface: SplitStrategy

```java
import models.User;

interface SplitStrategy:
        +

calculateSplits(payer:User,
                participants:List<User>,
                totalAmount:double,
                metadata:Map<String, Double>) ->List<Split>
```
> `metadata` carries the extra info each strategy needs:
> - EqualSplit → empty map (no extra info needed)
> - ExactSplit → `{ userId → exactAmount }`
> - PercentageSplit → `{ userId → percentage }`
> - ShareSplit → `{ userId → shares }`

### Class: EqualSplitStrategy
```java
class EqualSplitStrategy implements SplitStrategy:
  + calculateSplits(payer, participants, totalAmount, metadata)
      share = totalAmount / participants.size()
      return splits for everyone except payer
```

### Class: ExactSplitStrategy
```java
class ExactSplitStrategy implements SplitStrategy:
  + calculateSplits(payer, participants, totalAmount, metadata)
      validate sum of metadata values == totalAmount
      return splits using exact amounts from metadata
```

### Class: PercentageSplitStrategy
```java
class PercentageSplitStrategy implements SplitStrategy:
  + calculateSplits(payer, participants, totalAmount, metadata)
      validate sum of metadata values == 100.0
      return splits: amount = totalAmount * percentage / 100
```

### Class: ShareSplitStrategy
```java
class ShareSplitStrategy implements SplitStrategy:
  + calculateSplits(payer, participants, totalAmount, metadata)
      totalShares = sum of all metadata values
      return splits: amount = (theirShares / totalShares) * totalAmount
```

### Class: Group

```java
import models.User;

class Group:
        -id       :

String(final, UUID)
  -name     :

String(final)
  -members  :List<User>
  -expenses :List<Expense>

  +

getId() +

getName()
  +

addMember(user)
  +

addExpense(expense)
  +

getMembers()   ->List<User>
  +

getExpenses()  ->List<Expense>
```

### Class: SplitwiseService (Singleton orchestrator)

```java
import models.User;

class SplitwiseService:
        -instance   :

SplitwiseService(static, volatile)
  -users      :Map<String, User>
  -groups     :Map<String, Group>
  -balances   :Map<String, Double>
// key = "payerId:owerId"
// value = amount ower owes payer

  +

getInstance()                          ->SplitwiseService
  +

createUser(name, email)                ->User
  +

createGroup(name)                      ->Group
  +

addMemberToGroup(groupId, userId)
  +

addExpense(groupId, description,
           payerId, totalAmount,
           participantIds,
           splitStrategy,
           metadata)                   ->Expense
  +

settleUp(payerId, owerId, amount)
  +

getBalance(userId)                     ->double
  +

getBalanceWith(userId1, userId2)       ->double
  +

getGroupExpenses(groupId)              ->List<Expense>
```

---

## Implementation

### EqualSplitStrategy.calculateSplits
```
calculateSplits(payer, participants, totalAmount, metadata)
    share = totalAmount / participants.size()
    splits = []
    for each participant in participants:
        if participant != payer:
            splits.add(new Split(participant, share))
    return splits
```
> Payer already paid the full amount — they don't owe themselves anything.

---

### ExactSplitStrategy.calculateSplits
```
calculateSplits(payer, participants, totalAmount, metadata)
    sum = sum of all values in metadata
    if sum != totalAmount:
        throw IllegalArgumentException("Exact amounts must sum to total")

    splits = []
    for each participant in participants:
        if participant != payer:
            amount = metadata.get(participant.getId())
            splits.add(new Split(participant, amount))
    return splits
```

---

### PercentageSplitStrategy.calculateSplits
```
calculateSplits(payer, participants, totalAmount, metadata)
    totalPct = sum of all values in metadata
    if totalPct != 100.0:
        throw IllegalArgumentException("Percentages must sum to 100")

    splits = []
    for each participant in participants:
        if participant != payer:
            pct    = metadata.get(participant.getId())
            amount = totalAmount * pct / 100
            splits.add(new Split(participant, amount))
    return splits
```

---

### ShareSplitStrategy.calculateSplits
```
calculateSplits(payer, participants, totalAmount, metadata)
    // metadata = { userId → shares }
    totalShares = sum of all values in metadata

    splits = []
    for each participant in participants:
        if participant != payer:
            theirShares = metadata.get(participant.getId())
            amount      = (theirShares / totalShares) * totalAmount
            splits.add(new Split(participant, amount))
    return splits

// e.g. dinner 300, alice pays, bob=2 shares, carol=1 share, alice=1 share
// totalShares = 4
// bob   → (2/4) × 300 = 150
// carol → (1/4) × 300 = 75
// alice → excluded (payer)
```

---

### SplitwiseService.addExpense — core method
```
addExpense(groupId, description, payerId, totalAmount,
           participantIds, splitStrategy, metadata) -> Expense

    group        = getGroupOrThrow(groupId)
    payer        = getUserOrThrow(payerId)
    participants = participantIds.map(id -> getUserOrThrow(id))

    splits = splitStrategy.calculateSplits(payer, participants, totalAmount, metadata)

    expense = new Expense(description, payer, totalAmount, splits)
    group.addExpense(expense)

    for each split in splits:
        updateBalance(payer, split.getUser(), split.getAmount())

    return expense
```

---

### SplitwiseService.updateBalance — internal
```
updateBalance(payer, ower, amount)
    key = payer.getId() + ":" + ower.getId()
    balances.put(key, balances.getOrDefault(key, 0.0) + amount)
```
> Key format `"payerId:owerId"` encodes direction.
> `"alice:bob"` = bob owes alice.

---

### SplitwiseService.settleUp
```
settleUp(payerId, owerId, amount)
    key     = payerId + ":" + owerId
    current = balances.getOrDefault(key, 0.0)

    if amount > current:
        throw IllegalArgumentException("Settlement exceeds balance owed")

    balances.put(key, current - amount)
    if balances.get(key) == 0.0:
        balances.remove(key)    // clean up zero entries
```

---

### SplitwiseService.getBalance — net balance for one user
```
getBalance(userId) -> double
    net = 0.0
    for each entry in balances:
        parts = entry.getKey().split(":")
        if parts[0] == userId:  net += entry.getValue()   // others owe this user
        if parts[1] == userId:  net -= entry.getValue()   // this user owes others
    return net

// positive = others owe you
// negative = you owe others
```

---

### SplitwiseService.getBalanceWith
```
getBalanceWith(userId1, userId2) -> double
    owes1to2 = balances.getOrDefault(userId2 + ":" + userId1, 0.0)
    owes2to1 = balances.getOrDefault(userId1 + ":" + userId2, 0.0)
    return owes2to1 - owes1to2

// positive = userId2 owes userId1
// negative = userId1 owes userId2
```

---

## Verification — trace a dinner expense

| Step | Operation | Result |
|---|---|---|
| 0 | `addExpense(group, "Dinner", alice, 300, [alice,bob,carol], EQUAL)` | share=100. Splits: bob→100, carol→100. Alice excluded (payer). |
| 1 | `updateBalance(alice, bob, 100)` | `balances["alice:bob"] = 100`. Bob owes Alice 100. |
| 2 | `updateBalance(alice, carol, 100)` | `balances["alice:carol"] = 100`. Carol owes Alice 100. |
| 3 | `getBalance(alice)` | net = +100 + +100 = **+200**. Alice is owed 200. |
| 4 | `getBalance(bob)` | net = -100. Bob owes 100. |
| 5 | `settleUp("alice", "bob", 100)` | `balances["alice:bob"]` = 0 → removed. Bob cleared. |
| 6 | `getBalance(alice)` after settle | net = +100 (Carol only). |

---

## Key Design Decisions

**`SplitStrategy` is the core pattern.** Equal, Exact, Percentage, Share are four completely different algorithms. Without Strategy pattern, `addExpense` would have a giant if-else that breaks every time a new split type is added. With it — pass in any strategy, orchestrator never changes.

**Balance map key `"payerId:owerId"` encodes direction.** `"alice:bob"` = bob owes alice. Simpler than a separate `Balance` class. `getBalance` scans all keys — position 0 means you're owed, position 1 means you owe.

**Payer is excluded from splits.** Alice paid the dinner — she doesn't owe herself. Every `calculateSplits` implementation skips the payer. Most candidates forget this.

**`metadata` as `Map<String, Double>` keeps the interface uniform.** Each strategy reads what it needs. Orchestrator passes it through without knowing what it contains.

---

## Package Structure

```
src/
  Main.java
  model/
    models.User.java
    Split.java
    Expense.java
    Group.java
  strategy/
    SplitStrategy.java
    EqualSplitStrategy.java
    ExactSplitStrategy.java
    PercentageSplitStrategy.java
    ShareSplitStrategy.java
  service/
    SplitwiseService.java
```

---

## How to Run

```bash
javac -d out -sourcepath src $(find src -name "*.java")
java -cp out Main
```

---

## Extensibility

**Add debt simplification (minimize transactions)**
After all expenses are added, run a min-cash-flow algorithm on the balance map. Build a net balance per user, then use DFS/greedy to pair up creditors and debtors with the fewest transactions. This is purely a post-processing step — expense creation and split logic are completely untouched.

**Add expense categories (food, travel, rent)**
Add `category: ExpenseCategory` enum to `Expense`. Pass it in `addExpense`. Add `getExpensesByCategory(groupId, category)` filter to `SplitwiseService`. No structural changes.

**Add notifications when expense is added**
Introduce `NotificationService` interface with `notify(split)`. `SplitwiseService` calls it after each `updateBalance`. `EmailNotificationService` implements it. Same pattern as the meeting scheduler.

**Add a new split type**
Implement `SplitStrategy` as `ShareSplitStrategy` (or any other). Pass it to `addExpense`. Zero changes to `SplitwiseService`, `Expense`, or existing strategies.
