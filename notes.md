Can only rotate 90 clockwise or flip

```java
PentagoMove(int x, int y, int aSwap, int bSwap, int playerId)
```

int x, int y: place the piece

aSwap: Quadrant number: 0,1,2,3

bSwap: 0-Rotate 1-flip



# 0. Defence

### 1. When opponent's two pieces are connected

Try to block the centre at other quadrant

# 1. find all neighbour



# 2. Do monto Carlo with UCT



# problems

decodeMove->每次加的node都是一样的



Introduction

Motivation

Theoretical Basis

Implementation

Analysis

Further Improvements

```java
ArrayList moves <-- all legal moves that current state
MCST.root.children <-- moves
promissing_node <-- find one most promissing node from root.children
promissing_node.child e<-- expand the node by add a child to the promissing node;
run default policy on the child node:
	while(!e.state.gameover()){
    e.state.process(e.legal_move)
  }
e.propgate()
```



Q' = Q(s,a) + c
$$
Q'=Q(s,a)+c\sqrt{\frac{\log{n}(s)}{n(s,a)}}
$$

$$
Q(s,a)=\frac{WinScore}{RunTimes}
$$
