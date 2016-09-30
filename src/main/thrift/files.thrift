namespace java fr.insa.tc.tocent
namespace go greeter
namespace js greeter

service FileServer {
  string download(1: string name)
  bool upload(1: string name, 2: string data)
  bool rm(1: string name)
  list<string> ls()
}