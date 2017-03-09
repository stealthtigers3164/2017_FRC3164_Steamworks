from networktables import NetworkTables
from SimpleWebSocketServer import SimpleWebSocketServer, WebSocket


# sudo pip3 install git+https://github.com/dpallot/simple-websocket-server.git

ntAddress = "jrue.local"
#ntAddress = "roborio-3164-frc.local"

print("Connecting to roboRIO\n")
NetworkTables.initialize(ntAddress)
nt = NetworkTables.getTable("websocket")
'''
nt.putNumber("hue.min", 20);
nt.putNumber("hue.max", 100);

nt.putNumber("sat.min", 55);
nt.putNumber("sat.max", 150);

nt.putNumber("val.min", 30);
nt.putNumber("val.max", 230);
'''
clients = []


class SimpleSocket(WebSocket):

    def handleMessage(self):
        #print("message recieved from: " + self.address[0] + " | message: " + self.data)
        packet = self.data.split("|")
        values = ["hue", "sat", "val"]
        if packet[0] == "grip":
            if packet[1] in values:
                num = packet[2].split(":")
                nt.putNumber(packet[1] + ".min", num[0])
                nt.putNumber(packet[1] + ".max", num[1])
                message = "grip|" + packet[1] + "|" + str(nt.getNumber(packet[1] + ".min")) + ":" + str(nt.getNumber(packet[1] + ".max"))
                self.sendMessage(message)
            elif packet[1] == "all":
                for mod in values:
                    message = "grip|" + mod + "|" + str(nt.getNumber(mod + ".min")) + ":" + str(nt.getNumber(mod + ".max"))
                    print(message)
                    self.sendMessage(message)
        #for client in clients:
        # if client != self:
        #client.sendMessage(self.address[0] + u' - ' + self.data)

    def handleConnected(self):
        print(str(self.address) + 'connected')

        for client in clients:
            client.sendMessage(self.address[0] + u' - connected')
        clients.append(self)
        self.sendMessage("dont leave")

    def handleClose(self):
        clients.remove(self)
        print(str(self.address) + 'closed')
        for client in clients:
            client.sendMessage(self.address[0] + u' - disconnected')


server = SimpleWebSocketServer('', 8001, SimpleSocket)
server.serveforever()
