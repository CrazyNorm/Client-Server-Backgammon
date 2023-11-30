```xml

<message idem_key="[UID]">
    <join name="[PLAYER NAME" opponent="[HUMAN | AI]">
    <!-- 
     request specific player?
     AI difficulty?
     game setup?
     -->
    </join>
</message>
```

<br>
<hr>
<br>

Client:
```xml
<message idem_key="[UID]">
    <turn player="[WHITE | BLACK]">
        <domino side1="1" side2="2"/>
        <domino side1="double" side2="double"/>
        
        <move start="s" end="e"/>
        <move start="s" end="e"/>
        <!-- <move .../> -->
        <!-- <move .../> -->
    </turn>
</message>
```

Server response:
```xml
<respose to="idem_key">
    <approve/>
    <!-- OR -->
    <deny reason="REASON FOR REJECTION"/>
</respose>
```

<br>
<hr>
<br>

Server:
```xml
<message>
    <turn>
        ...
    </turn>
</message>
```

Client response:
```xml
<response>
    <hash value="HASH OF GAME STATE"/>
</response>
```

Server response:
```xml
<response>
    <approve/>
    <!-- OR -->
    <deny reason="Inconsistent"/>
</response>
```

Spectator clients would receive these messages from the server with state changes,
but spectators would never take their own turn (i.e. never send a "turn" message to the server)


<hr>

Server:

```xml

<message>
    <pieceList colour="[WHITE | BLACK]">
        <index>1</index>
        <index>1</index>
        <index>2</index>
        <index>2</index>
        <!-- ... -->
    </pieceList>
    <pieceList>
        ...
    </pieceList>
    
    <hand colour="[WHITE | BLACK]">
        <domino side1="1" side2="2" available="[true | false]"/>
        <domino/>
        <!-- ... -->
    </hand>
    <hand>
        ...
    </hand>
</message>
```

Client response:
```xml
<response>
    <hash value="HASH OF GAME STATE"/>
</response>
```

Server response:
```xml
<response>
    <approve/>
    <!-- OR -->
    <deny reason="Inconsistent"/>
</response>
```

<br>
<hr>
<br>

Server:
```xml
<message>
    <win player="[WHITE | BLACK]" type="[1 | 2 | 3]"/>
    <swap/>
    <nextTurn player="[WHITE | BLACK]"/>
</message>
```

Client response:
```xml
<response>
    <acknowledge/>
</response>
```

<br>
<hr>
<br>

Keep alive?
```xml
<ka/>
```

Can also assume connection is dropped if a message gets no response after retrying x times.
But need keep alive messages when waiting on a message (e.g. server waiting for client to take their turn)

<br>
<hr>
<br>

Chat?
```xml
<message>
    <chat from="sender name" to="recipient name">
        Lorem ipsum dolor ...
    </chat>
</message>
```