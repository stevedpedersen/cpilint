xquery version "3.1";

declare namespace bpmn2 = "http://www.omg.org/spec/BPMN/20100524/MODEL";
declare namespace ifl = "http:///com.sap.ifl.model/Ifl.xsd";

declare function local:getPropertyValue($element as element(), $propertyKey as xs:string) as xs:string? {
  let $property := ($element/bpmn2:extensionElements/ifl:property[key = $propertyKey]/value)[1]
  return if (exists($property)) then string($property) else ""
};

declare function local:isIDMapper($callActivity as element()) as xs:boolean {
  exists($callActivity/bpmn2:extensionElements/ifl:property[key = "activityType" and value = "IDMapper"])
};

for $callActivity in //bpmn2:callActivity
return
  if (local:isIDMapper($callActivity)) then
    (string($callActivity/@name), string($callActivity/@id), "IDMAPPER", "IDMAPPER")
  else
    let $name := string($callActivity/@name)
    let $id := string($callActivity/@id)
    let $activityType := local:getPropertyValue($callActivity, "activityType")
    let $subActivityType := local:getPropertyValue($callActivity, "subActivityType")
    return ($name, $id, $activityType, $subActivityType)
