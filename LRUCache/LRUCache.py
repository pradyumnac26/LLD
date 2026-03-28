class Node : 
    def __init__(self, key:int, val:int) : 
        self.key = key 
        self.val = val 
        self.next = None 
        self.prev = None 


class LRUCache : 
    def __init__(self, capacity : int) : 
        self.capacity = capacity 
        self.hmap = {} # key and the Node
        self.head = Node(-1, -1)
        self.tail = Node(-1, -1)
        self.head.next = self.tail
        self.tail.prev = self.head

    def insert(self, node : Node) :
        self.hmap[node.key] =  node 
        node.next = self.head.next 
        node.next.prev = node
        self.head.next = node 
        node.prev = self.head

    def remove(self, node : Node) : 
        del self.hmap[node.key]
        node.prev.next = node.next 
        node.next.prev = node.prev
    
    def get(self, key: int) -> int: 
        if key in self.hmap : 
            node = self.hmap[key]
            self.remove(node) 
            self.insert(node)
            return node.val 
        return -1

    def put(self, key:int, val:int) -> int : 
        if key in self.hmap : 
            self.remove(self.hmap[key]) 
        if len(self.hmap) == self.capacity : 
            self.remove(self.tail.prev)
        self.insert(Node(key, val))


if __name__ == "__main__":
    cache = LRUCache(capacity=3)

    cache.put(1, 10)
    cache.put(2, 20)
    cache.put(3, 30)

    print(cache.get(1))   # 10  → key 1 is now MRU

    cache.put(4, 40)      # capacity hit → evicts key 2 (LRU)

    print(cache.get(2))   # -1  → already evicted
    print(cache.get(3))   # 30
    print(cache.get(4))  


