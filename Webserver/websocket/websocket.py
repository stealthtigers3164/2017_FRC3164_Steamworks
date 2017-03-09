from networktables import NetworkTables
from SimpleWebSocketServer import SimpleWebSocketServer, WebSocket
import json
import threading
import time
from pprint import pprint 
# sudo pip3 install git+https://github.com/dpallot/simple-websocket-server.git

#ntAddress = "jrue.local"
ntAddress = "roborio-3164-frc.local"

fileName = "/home/pi/2017_FRC3164_Steamworks/Webserver/websocket/data.json"
#fileName = "data.json"

clients = []


class SimpleSocket(WebSocket):

    def handleMessage(self):
        # print("message recieved from: " + self.address[0] + " | message: " +
        # self.data)
        packet = self.data.split("|")
        values = ["hue", "sat", "val"]
        if packet[0] == "grip":
            if packet[1] in values:
                num = packet[2].split(":")
                nt.putNumber(packet[1] + ".min", num[0])
                nt.putNumber(packet[1] + ".max", num[1])
                message = "grip|" + packet[1] + "|" + str(nt.getNumber(
                    packet[1] + ".min")) + ":" + str(nt.getNumber(packet[1] + ".max"))
                self.sendMessage(message)
                time.sleep(0.1)
                toWrite = [{"hue": (nt.getNumber("hue.min"), nt.getNumber("hue.max")), "sat": (nt.getNumber("sat.min"), nt.getNumber("sat.max")), "val": (nt.getNumber("val.min"), nt.getNumber("val.max"))}]
                with open(fileName, 'w+') as outfile:
                    json.dump(toWrite, outfile)
                    outfile.close()
            elif packet[1] == "all":
                for mod in values:
                    message = "grip|" + mod + "|" + \
                        str(nt.getNumber(mod + ".min")) + ":" + \
                            str(nt.getNumber(mod + ".max"))
                    print(message)
                    self.sendMessage(message)
        # for client in clients:
        # if client != self:
        # client.sendMessage(self.address[0] + u' - ' + self.data)

    def handleConnected(self):
        #nt_check()
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


def json_thread(arg):
    ntW = NetworkTables.getTable("websocket")
    while 1:
        #print(str(nt.getNumber("hue.min")))
        
        #print(json.dumps([{"hue": (nt.getNumber("hue.min"), ntW.getNumber("hue.max"))}, {"sat": (ntW.getNumber("sat.min"), ntW.getNumber("sat.max"))}, {"val": (ntW.getNumber("val.min"), ntW.getNumber("val.max"))}]))
        #toWrite = json.dumps([{"hue": (ntW.getNumber("hue.min"), ntW.getNumber("hue.max"))}, {"sat": (ntW.getNumber("sat.min"), ntW.getNumber("sat.max"))}, {"val": (ntW.getNumber("val.min"), ntW.getNumber("val.max"))}])
        # with open('data.txt', 'w') as outfile:
        #    json.dump(toWrite, outfile)
        print("Writing JSON File")
        time.sleep(5)


def nt_check():
    while(nt.isConnected == False):
        time.sleep(0.01)
    print("Connected to NT")
    with open(fileName, encoding='utf-8') as data_file:
        data = json.loads(data_file.read())
        nt.putNumber("hue.min", int(data[0]["hue"][0]));
        nt.putNumber("hue.max", int(data[0]["hue"][1]));

        nt.putNumber("hue.min", int(data[0]["sat"][0]));
        nt.putNumber("hue.max", int(data[0]["sat"][1]));

        nt.putNumber("val.min", int(data[0]["val"][0]));
        nt.putNumber("val.max", int(data[0]["val"][1]));
        
        


def main():
    print("Connecting to roboRIO\n")
    global nt
    NetworkTables.initialize(ntAddress)
    nt = NetworkTables.getTable("websocket")
    

    '''
    time.sleep(1)
    print("Putting False Values")
    nt.putNumber("hue.min", 20)
    nt.putNumber("hue.max", 100)

    nt.putNumber("sat.min", 55)
    nt.putNumber("sat.max", 150)

    nt.putNumber("val.min", 30)
    nt.putNumber("val.max", 230)
    '''


    nt_check()
    #thread = threading.Thread(target = json_thread, args = (10, ))
    #thread.start()

    server = SimpleWebSocketServer('', 8001, SimpleSocket)
    server.serveforever()


if __name__ == '__main__':
    main()

# with open('data.txt', 'w') as outfile:
#    json.dump(data, outfile)
