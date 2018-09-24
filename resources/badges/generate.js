/**
 * This script helps to generate constants for badges names and localized strings for titles and descriptions
 */

const NAMES = [
    // user
    "new_user",
    // data_collection
    "data_collector_level0", // data_collection
    "data_collector_level1", // data_collection
    "data_collector_level2", // data_collection
    "data_collector_level3", // data_collection

    "biker_level1", // bike_usage
    "biker_level2", // bike_usage
    "biker_level3", // bike_usage

    "public_mobility_level1", // public_transport_usage
    "public_mobility_level2", // public_transport_usage
    "public_mobility_level3", // public_transport_usage

    "bike_surfer_level1", // bike_usage
    "bike_surfer_level2", // bike_usage
    "bike_surfer_level3", // bike_usage

    "tpl_surfer_level1", // public_transport_usage
    "tpl_surfer_level2", // public_transport_usage
    "tpl_surfer_level3", // public_transport_usage

    "multi_surfer_level1", // sustainability
    "multi_surfer_level2", // sustainability
    "multi_surfer_level3", // sustainability

    "ecologist_level1", // sustainability
    "ecologist_level2", // sustainability
    "ecologist_level3", // sustainability

    "healthy_level1", // sustainability
    "healthy_level2", // sustainability
    "healthy_level3", // sustainability

    // cost_savings
    "money_saver_level1", // cost_savings
    "money_saver_level2", // cost_savings
    "money_saver_level3" // cost_savings
];


const LOCALIZATION_IT = [{
"title": "Nuovo Utente!! ",
"before": "Appena ti iscrivi ed installi l’APP guadagni questo badge",
"after": "Benvenuto e pronto a partire con una mobilità più sostenibile ed ecologica!! Condividi i tuoi risultati con tuoi amici e sfidali in competizioni personali!"
}, {
"title": "Rilevatore in erba!!",
"before": "Quando inizi ad inserire i dati di rilevamento dei tuoi modi di trasporto, ottieni questo badge",
"after": "Ottimo!! Hai iniziato a registrare i tuoi percorsi, così potrai collezionare nuovi badges e crescere il tuo punteggio!! Continua così! Le statistiche che ti forniamo ti mostreranno come puoi risparmiare tempo, soldi e salute, muovendoti in modo sostenibile!!"
}, {
"title": "Rilevatore – 1 stella!!",
"before": "Quando hai registrato attività in una settimana per ogni giorno, otterrai questo badge.",
"after": "Ottimo!! Hai registrato i tuoi percorsi in una settimana intera, così potrai collezionare nuovi badges e crescere il tuo punteggio!! Continua così! Le statistiche che ti forniamo ti mostreranno come puoi risparmiare tempo, soldi e salute, muovendoti in modo sostenibile!!"
}, {
"title": "Rilevatore – 2 stelle!!",
"before": "Quando hai registrato attività in due settimane per ogni giorno, otterrai questo badge.",
"after": "Ottimo!! Hai registrato i tuoi percorsi in due intere settimane, così potrai collezionare nuovi badges e crescere il tuo punteggio!! Continua così! Le statistiche che ti forniamo ti mostreranno come puoi risparmiare tempo, soldi e salute, muovendoti in modo sostenibile!!"
}, {
"title": "Rilevatore – 3 stelle!!",
"before": "Quando hai registrato attività in un mese per ogni giorno, otterrai questo badge.",
"after": "Ottimo!! Hai registrato i tuoi percorsi in un intero mese, così potrai collezionare nuovi badges e crescere il tuo punteggio!! Continua così! Le statistiche che ti forniamo ti mostreranno come puoi risparmiare tempo, soldi e salute, muovendoti in modo sostenibile!!"
}, {
"title": "Biker - 1 stella!!",
"before": "Inizia ad utilizzare la tua bici in città!! Usa la bici tre volte in una settimana ed otterrai questo badge.",
"after": "Ottimo! Con i tuoi spostamenti cittadini in bici sei entrato nel gruppo dei bikers!!"
}, {
"title": "Biker - 2 stelle!!",
"before": "Riutilizza la bici altre tre volte in città nella prossima settimana ed otterrai questo badge.",
"after": "Bene! Sei riuscito ad utilizzare la bici in città sei volte in due settimane!! Aria fresca e movimento!!"
}, {
"title": "Biker - 3 stelle!!",
"before": "Riutilizza la bici in città altre sei volte nelle prossime due settimane ed otterrai questo badge!!",
"after": "Bene! Sei riuscito ad utilizzare la bici dodici volte in quattro settimane dentro la tua città!! Aria fresca e movimento!!"
}, {
"title": "Mobilità Collettiva – 1 stella!!",
"before": "Blocca la tua auto ed utilizza il trasporto pubblico urbano(tramvia, bus, metro)! Al primo utilizzo del trasporto pubblico otterrai questo badge.",
"after": "Grande! Hai utilizzato per la prima volta il trasporto pubblico! Sei più sostenibile risparmiando soldi e tempo!  ",
}, {
"title": "Mobilità Collettiva – 2 stelle!!",
"before": "Blocca la tua auto ed utilizza il trasporto pubblico urbano(tramvia, bus, metro)! Al quinto utilizzo del trasporto pubblico otterrai questo badge.",
"after": "Grande! Hai utilizzato per sei volte il trasporto pubblico! Sei più sostenibile risparmiando soldi e tempo!"
}, {
"title": "Mobilità Collettiva – 3 stelle!!",
"before": "Blocca la tua auto ed utilizza il trasporto pubblico urbano(tramvia, bus, metro)! Al decimo utilizzo del trasporto pubblico otterrai questo badge.",
        "after": "Grande! Hai utilizzato per dieci volte il trasporto pubblico! Sei più sostenibile risparmiando soldi e tempo!"
}, {
"title": "Bike Surfer – 1 stella!!",
"before": "Utilizza la bici per almeno 10 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 10 km in bici all’interno della tua città. "
}, {
"title": "Bike Surfer – 2 stelle!!",
"before": "Utilizza la bici per almeno 50 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 50 km in bici all’interno della tua città. "
}, {
"title": "Bike Surfer – 3 stelle!!",
"before": "Utilizza la bici per almeno 100 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 100 km in bici all’interno della tua città. "
}, {
"title": "TPL Surfer – 1 stella!!",
"before": "Utilizza l’autobus per almeno 25 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 25 km in autobus all’interno della tua città. "
}, {
"title": "TPL Surfer – 2 stelle!!",
"before": "Utilizza l’autobus per almeno 100 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 100 km in autobus all’interno della tua città. "
}, {
"title": "TPL Surfer – 3 stelle!!",
"before": "Utilizza l’autobus per almeno 200 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 200 km in autobus all’interno della tua città. "
}, {
"title": "Multi Surfer – 1 stella!!",
"before": "Utilizza mezzi sostenibili per almeno 100 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 100 km con più mezzi sostenibili!! Stai dando un grande aiuto alla collettività!!"
}, {
"title": "Multi Surfer – 2 stelle!!",
"before": "Utilizza mezzi sostenibili per almeno 250 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 250 km con più mezzi sostenibili!! Stai dando un grande aiuto alla collettività!!"
}, {
"title": "Multi Surfer – 3 stelle!!",
"before": "Utilizza mezzi sostenibili per almeno 500 km in ambito urbano ed otterrai questo badges! Hai dato un grande apporto alla mobilità sostenibile nella tua città!",
"after": "Grande! Hai percorso 500 km con più mezzi sostenibili!! Stai dando un grande aiuto alla collettività!!"
}, {
"title": "Ecologista – 1 stella!!",
"before": "Evita le emissioni per 25 kg di CO2 in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai evitato di emettere 25 kg di CO2 all’interno della tua città.Hai dato un grande apporto al mantenimento della qualità dell’aria nella tua città!"
}, {
"title": "Ecologista – 2 stelle!!",
"before": "Evita le emissioni per 50 kg di CO2 in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai evitato di emettere 50 kg di CO2 all’interno della tua città.Hai dato un grande apporto al mantenimento della qualità dell’aria nella tua città!"
}, {
"title": "Ecologista – 3 stelle!!",
"before": "Evita le emissioni per 100 kg di CO2 in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai evitato di emettere 100 kg di CO2 all’interno della tua città.Hai dato un grande apporto al mantenimento della qualità dell’aria nella tua città!"
}, {
"title": "Salutista – 1 stella!!",
"before": "Consuma un totale di 750 calorie grazie ai tuoi spostamenti “attivi” in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai consumato 750 calorie grazie ai tuoi spostamenti all’interno della tua città.Non inquinando ti stai tenendo in forma!!"
}, {
"title": "Salutista – 2 stelle!!",
"before": "Consuma un totale di 2.250 calorie grazie ai tuoi spostamenti “attivi” in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai consumato 2.250 calorie grazie ai tuoi spostamenti all’interno della tua città.Non inquinando ti stai tenendo in forma!!"
}, {
"title": "Salutista – 3 stelle!!",
"before": "Consuma un totale di 4.500 calorie grazie ai tuoi spostamenti “attivi” in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai consumato 4.500 calorie grazie ai tuoi spostamenti all’interno della tua città.Non inquinando ti stai tenendo in forma!!"
}, {
"title": "Risparmiatore – 1 stella!!",
"before": "Risparmia un totale di 6 € grazie ai tuoi spostamenti sostenibili in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai evitato una spesa totale di 6 € grazie ai tuoi spostamenti sostenibili all’interno della tua città.Risparmiando, hai dato un grande apporto alla mobilità sostenibile cittadina!"
}, {
"title": "Risparmiatore – 2 stelle!!",
"before": "Risparmia un totale di 12 € grazie ai tuoi spostamenti sostenibili in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai evitato una spesa totale di 12 € grazie ai tuoi spostamenti sostenibili all’interno della tua città.Risparmiando, hai dato un grande apporto alla mobilità sostenibile cittadina!"
}, {
"title": "Risparmiatore – 3 stelle!!",
"before": "Risparmia un totale di 24 € grazie ai tuoi spostamenti sostenibili in ambito urbano ed otterrai questo badges! ",
"after": "Grande! Hai evitato una spesa totale di 24 € grazie ai tuoi spostamenti sostenibili all’interno della tua città.Risparmiando, hai dato un grande apporto alla mobilità sostenibile cittadina!"
}, {
"title": "Disseminatore – 1 stella!!",
"before": "Ottieni questo badge invitando 10 amici ad utilizzare l’APP Good_Go.Dissemina la cultura della sostenibilità giocando con i tuoi amici!!",
"after": "Ottimo! Hai invitato 10 amici ad usare l’APP Good_Go"
}, {
"title": "Disseminatore – 2 stelle!!",
"before": "Ottieni questo badge invitando 25 amici ad utilizzare l’APP Good_Go.Dissemina la cultura della sostenibilità giocando con i tuoi amici!!",
"after": "Ottimo! Hai invitato 25 amici ad usare l’APP Good_Go"
}, {
"title": "Disseminatore – 3 stelle!!",
"before": "Ottieni questo badge invitando 50 amici ad utilizzare l’APP Good_Go.Dissemina la cultura della sostenibilità giocando con i tuoi amici!!",
"after": "Ottimo! Hai invitato 50 amici ad usare l’APP Good_Go"
}];

const printConst = (mapName, idPrefix, type = "string") => {
    console.log(`${mapName} = new HashMap<String, Integer>();`);
    NAMES.map(id => console.log(`${mapName}.put("${id}", R.${type}.${idPrefix}${id});`));
};
const printStrings = (idPrefix, key) => {
    NAMES.map((id, index) => console.log(`<string name="${idPrefix}${id}">${LOCALIZATION_IT[index][key]}</string>`));
};

const printAllBadges = () => {
    NAMES.map ( name => console.log(`badges.add(new Badge("${name}"));`))
}
console.log("// TITLE");
printConst("NAME_TITLE_MAP", "badge_title_");
console.log("// Before");
printConst("NAME_BEFORE_MAP", "badge_before_");
console.log("// After");
printConst("NAME_AFTER_MAP", "badge_after_");
console.log("// Icons");
printConst("NAME_ICON_MAP", "badge_ic_", "drawable");

console.log("============================Strings==================================");
console.log("")
console.log("<!-- ================== -->");
console.log("<!-- ======Badges====== -->");
console.log("<!-- ================== -->");
console.log("<!-- titles -->");
printStrings("badge_title_", "title");
console.log("<!-- before -->");
printStrings("badge_before_", "before");
console.log("<!-- after -->");
printStrings("badge_after_", "after");
console.log("<!-- ================== -->");
console.log("<!-- ==End of badges == -->");
console.log("<!-- ================== -->");

console.log("============================Badges List ==================================");
console.log("")
printAllBadges();

console.log("============================Icon Names ==================================");


