########################################
# locals.tf
########################################
locals {
  ########################################
  # Client Username & Password
  ########################################
  client_usernames = [
    { 
      name             = var.time_client_username, 
      acl_profile_name = "time-acl-profile",
      endpoint_override = true,
      password         = var.time_client_password,
    },
    { 
      name             = "djs-ssl-client.jnj.com", 
      acl_profile_name = "default",
      endpoint_override = false,
      password         = null,
    },
  ]

  # Queue owner
  queue_owner = var.time_client_username

  ########################################
  # MaintenanceOrder Events for Enablon
  ########################################
  workorder_events_enablon = [
    "Approved",
    "OpPhseCtrlActvtd",
    "OpPhseCtrlDactvtd",
    "OrdPhseCtrlActvtd",
    "OrdPhseCtrlDactvtd",
    "SetReadyForExec",
    "SetToDeletnFlagged",
    "SetToInPlanning",
    "SetToInPreparation",
    "SetToRdyForSchedg",
    "SetToWorkStarted",
    "SetToWrkNotPerfrmd",
    "SubmdForApproval",
  ]
  workorder_topics_enablon = [
    for event in local.workorder_events_enablon :
    "${var.s4_prefix}/maintenanceorder/v1/MaintenanceOrder/${event}/${var.wildcard}"
  ]
  zjnj = "zjnj/s4/beh"

  /*************************************************************
   * Each queue’s value is now a map:
   * {
   *   topics      = [ ... ],
   *   access_type = <string or null>
   * }
   * If access_type is null, we fallback to exclusive or non-exclusive
   * depending on max_redelivery_count.
   *************************************************************/

  ########################################
  # Queues with max_redelivery_count = 0
  ########################################
  queues_with_max_redelivery_count_0 = {

    ################ Initial Load / Delta Load Queues ################
    "q/IM/TIME/Customer/CFIN" = {
      topics      = ["im/time/customer", "im/time/vendor"],
      access_type = null
    },
    "q/IM/TIME/Material/CFIN" = {
      topics      = ["im/time/material"],
      access_type = null
    },
    "q/IM/TIME/Customer/JJCC" = {
      topics      = ["im/time/customer","im/time/vendor"],
      access_type = null
    },
    "q/IM/TIME/Material/JJCC" = {
      topics      = ["im/time/material"],
      access_type = null
    },
    "q/IM/TIME/Customer/SFDC" = {
      topics      = ["im/time/customer", "im/time/vendor"],
      access_type = null
    },
    "q/IM/TIME/Customer/Partner/SFDC" = { # For child record processing (account -> partner)
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/Customer/eLIMS" = {
      topics      = ["im/time/customer"],
      access_type = null
    },
    "q/IM/TIME/Vendor/eLIMS" = {
      topics      = ["im/time/vendor"],
      access_type = null
    },
    "q/IM/TIME/Material/eLIMS" = {
      topics      = ["im/time/material"],
      access_type = null
    },
    ################ END Initial Load / Delta Load Queues ################

    "q/IM/TIME/Batch/eLIMS" = {
      topics      = ["im/time/batch"],
      access_type = null
    },
    "q/IM/TIME/Equipment/eLIMS" = {
      topics      = ["im/time/equipment"],
      access_type = null
    },
    "q/IM/TIME/InspectionLot/eLIMS" = {
      topics = [
        "${var.s4_prefix}/inspectionlot/v1/InspectionLot/Created/${var.wildcard}",
        "${var.s4_prefix}/inspectionlot/v1/InspectionLot/Changed/${var.wildcard}",
        "${var.s4_events}/${local.zjnj}/InspUsageDec/${var.wildcard}",
        "${var.s4_events}/ce/${local.zjnj}/InspUsageDec/update/${var.wildcard}"
      ],
      access_type = null
    },
    "q/IM/TIME/UsageDecision/eLIMS" = {
      topics = [
        "${var.s4_prefix}/usagedecision/v1/UsageDecision/Created/${var.wildcard}",
        "${var.s4_prefix}/usagedecision/v1/UsageDecision/Updated/${var.wildcard}"
      ],
      access_type = null
    },
    "q/IM/TIME/SalesOrderAck/JJCC" = {
      topics      = ["im/time/salesorder/ack"],
      access_type = null
    },
    "q/IM/Pendulum/PricingData/TIME" = {
      topics      = [],
      access_type = null
    },
    "q/IM/OneNetwork/POConfirmChange/TIME" = {
      topics      = ["im/time/purchaseorder/confirmation", "im/time/purchaseorder/change"],
      access_type = null
    },
    "q/IM/TIME/AssetData/Enablon" = {
      topics = [
        "${var.s4_prefix}/equipment/v1/Equipment/${var.wildcard}",
        "${var.s4_prefix}/functionallocation/v1/FunctionalLocation/${var.wildcard}",
      ],
      access_type = null
    },
    "q/IM/TIME/WorkOrder/Enablon" = {
      topics      = local.workorder_topics_enablon,
      access_type = null
    },
    "q/IM/TIME/POOutput/OneNetwork" = {
      topics      = ["im/time/po"],
      access_type = null
    },
    "q/IM/TIME/SalesOrderAck/EU2" = {
      # topics      = ["im/time/salesorder/ack"], # temporarily unsubscribed since this iFlow won't be ready until R2
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/Invoice/EU2" = {
      topics      = ["im/time/invoice"],
      access_type = null
    },
    "q/IM/TIME/Invoice/JJCC" = {
      topics      = ["im/time/invoice"],
      access_type = null
    },
    "q/IM/CPDN/InventoryAdjustment/TIME" = {
      topics      = [],
      access_type = null
    },
    "q/IM/CPDN/InventoryTransfer/TIME" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/POOutputPDF/OneNetwork" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/StockConfirmation/CPDN" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/OrderType/ZDR/ZCR/CPDN" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/OrderType/ZKE/CPDN" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/ASNDeliveryRequest/Elims" = {
      topics      = ["im/time/asn"],
      access_type = null
    },
    "q/IM/TIME/PO/EU2" = {
      topics      = ["im/time/po"],
      access_type = null
    },
    "q/IM/Gateway/FreightOrder/TIME" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/ReturnOrderType/ZRE2/CPDN" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/CustomerReturns/CPDN" = {
      topics      = ["${var.s4_prefix}/customerreturn/v1/CustomerReturn/Created/>"],
      access_type = null
    },
    "q/IM/TIME/MaterialDocument/EU2" = {
      topics      = ["${var.s4_prefix}/materialdocument/v1/MaterialDocument/Created/>"],
      access_type = null
    },
    "q/IM/TIME/MatDocStore/EU2" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/SO/Create" = {
      topics      = [],
      access_type = "exclusive"   # As requested by dev
    },
  }

  ########################################
  # Queues with max_redelivery_count = 1
  ########################################
  queues_with_max_redelivery_count_1 = {
    "q/IM/TIME/SalesOrder/CQUENCE" = {
      topics = [
        "${var.s4_prefix}/salesorder/v1/SalesOrder/Created/${var.wildcard}",
        "${var.s4_prefix}/salesorder/v1/SalesOrder/Changed/${var.wildcard}"
      ],
      access_type = null
    },
    "q/IM/TIME/SalesOrderAckCartID/CQUENCE" = {
      topics      = ["im/time/salesorder/ack/cartid"],
      access_type = null
    },
    "q/IM/TIME/SupplierInvoice/CQUENCE" = {
      topics      = ["${var.s4_prefix}/supplierinvoice/v1/SupplierInvoice/Created/${var.wildcard}"],
      access_type = null
    },
    "q/IM/CQUENCE/SalesOrder/TIME" = {
      topics      = ["im/time/salesorder/create"],
      access_type = null
    },
    "q/IM/TIME/GoodsReceipt/AT" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/InboundDelivery/AT" = {
      topics      = ["${var.s4_prefix}/inbounddelivery/v1/InboundDelivery/Created/${var.wildcard}"],
      access_type = null
    },
    "q/IM/TIME/MaterialDocument/AT" = {
      topics      = ["${var.s4_prefix}/materialdocument/v1/MaterialDocument/Created/${var.wildcard}"],
      access_type = null
    },
    "q/IM/TIME/OutboundDelivery/AT" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/CustomerInvoice/AT" = {
      topics      = [],
      access_type = null
    },
    "q/IM/TIME/CalibrationDates/Elims" = {
      topics = [
        "${var.s4_prefix}/maintenanceorder/v1/MaintenanceOrder/SetToTechCompleted/${var.wildcard}",
        "${var.s4_events}/ce/${local.zjnj}/InspUsageDec/update/${var.wildcard}"
      ],
      access_type = null
    },
  }

  ########################################
  # Queues with max_redelivery_count = 2
  ########################################
  queues_with_max_redelivery_count_2 = {}

  ########################################
  # Queues with max_redelivery_count = 3
  ########################################
  queues_with_max_redelivery_count_3 = {
    "q/IM/TIME/ASNDeliveryRequest/OpenText" = {
      topics      = ["im/time/asn"],
      access_type = null
    },
  }

  ########################################
  # Collect all queue groups in a map
  ########################################
  queues_by_max_redelivery_count = {
    0 = local.queues_with_max_redelivery_count_0
    1 = local.queues_with_max_redelivery_count_1
    2 = local.queues_with_max_redelivery_count_2
    3 = local.queues_with_max_redelivery_count_3
  }

  ################################################################
  # DMQs which follow "dm" + <queue_name>
  # Now include optional fields for the new module version
  ################################################################
  dmqs = flatten([
    for max_redelivery_count, queues_map in local.queues_by_max_redelivery_count : [
      for queue_name in keys(queues_map) : {
        name                = replace(queue_name, "q/", "dmq/"),
        owner               = local.queue_owner,
        egress_enabled      = true,
        ingress_enabled     = true,
        respect_ttl_enabled = true,

        # The next lines ensure the new optional fields have a value.
        # For DMQs, we usually set redelivery to false and 0 counts.
        dmq_name                = null               # let the module default to #DEAD_MSG_QUEUE
        redelivery_enabled      = false
        max_redelivery_count    = 0
        max_ttl                 = 0
        delivery_count_enabled  = false
      }
    ]
  ])

  /***************************************************************
   * Flatten main queues. If "access_type" is null, fallback:
   *   - "exclusive" when max_redelivery_count == 0
   *   - "non-exclusive" otherwise**                                  ** Maybe default all to exclusive instead? **
   ***************************************************************/
  queues = flatten([
    for max_redelivery_count, queues_map in local.queues_by_max_redelivery_count : [
      for queue_name, queue_config in queues_map : {
        name                 = queue_name,
        owner                = local.queue_owner,
        permission           = "no-access",
        dmq_name             = replace(queue_name, "q/", "dmq/"),
        egress_enabled       = true,
        ingress_enabled      = true,
        max_redelivery_count = max_redelivery_count,
        redelivery_enabled   = max_redelivery_count > 0,
        respect_ttl_enabled  = true,
        max_ttl              = 0,

        # If queue_config.access_type is null, we fallback to "non-exclusive"
        access_type = coalesce(queue_config.access_type, "non-exclusive")
      }
    ]
  ])

  ########################################
  # Topic Subscriptions for each queue
  ########################################
  subscriptions = flatten([
    for max_redelivery_count, queues_map in local.queues_by_max_redelivery_count : [
      for queue_name, queue_config in queues_map : [
        for topic in queue_config.topics : {
          queue_name         = queue_name,
          subscription_topic = topic
        }
      ]
    ]
  ])
}